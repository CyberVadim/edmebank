package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.SpectrumClient;
import com.edmebank.clientmanagement.dto.spectrum.ApplicantData;
import com.edmebank.clientmanagement.dto.spectrum.ApplicantRequest;
import com.edmebank.clientmanagement.dto.spectrum.checkUid.ResponseUidData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ReportData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ResponseData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.SourceData;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpectrumService {
    private final SpectrumClient spectrumClient;
    private final String authHeader = "AR-REST dXNlcl9pbnRlZ3JhdGlvbkBiYWtzZXQ6MTc0MTg1MjgwMDo5OTk5OTk5OTk6MzRKeWpZdXhQWnd6Z0JudThGQi9uQT09";
    private final ClientRepository clientRepository;
    private final List<String> idChekList = List.of(
            "check_person/extremist_list",
            "check_person/executive_proceeding_base",
            "check_person/fsin_wanted",
            "check_person/acc_stop",
            "check_person/wanted_criminal",
            "check_person/expired_passport_v2",
            "check_person/credit_history",
            "check_person/inoagent",
            "check_person/linkasso_manager_for",
            "check_person/person_inn"
    );

    public String getUid(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + clientId + " не найден"));

        ApplicantData applicantData = ApplicantData.builder()
                .lastName(client.getLastName())
                .firstName(client.getFirstName())
                .patronymic(client.getMiddleName())
                .birth(client.getDateOfBirth().toString())
                .passport(client.getPassportNumber())
                .passportDate(client.getPassportIssueDate().toString())
                .phone(client.getPhone())
                .inn(client.getInn())
                .build();


        ApplicantRequest request = new ApplicantRequest("MULTIPART", " ", applicantData);
        ResponseUidData responseUidData = spectrumClient.checkApplicant(authHeader, request);
        String uid = responseUidData.getData().get(0).getUid();
        return uid;
    }

    public ReportData fetchReport(String uid) {

        ResponseData response = spectrumClient.getReport(uid, false, false, authHeader);
        return response.getData() != null && response.getData().size() > 0 ? response.getData().get(0) : null;
    }

    public Boolean canRegisterClient(String uid) {
        ResponseData response = spectrumClient.getReport(uid, false, false, authHeader);

        if (response.getData() == null || response.getData().isEmpty()) {
            return false;
        }

        ReportData report = response.getData().get(0);
        return processCheckResults(report.getState().getSources());
    }

    public boolean isClientTerrorist() {

        return false;
    }

    private Boolean processCheckResults(List<SourceData> sources) {
        StringBuilder resultMessage = new StringBuilder();

        for (SourceData source : sources) {
            String state = source.getState();
            String id = source.get_id();


            if (!"OK".equals(state)) {
                if (idChekList.contains(id)){
                    String reason = (String) source.getData().get("reason");
                    resultMessage.append("state: ").append(state).append("\n")
                            .append("Проверка: ").append(getCheckDescription(id)).append("\n")
                            .append("Ответ: ").append(reason).append("\n\n");
                    log.info(resultMessage.toString());
                    return false;
                }
            }
        }

        return true;
    }

    // Метод для получения описания проверки
    private String getCheckDescription(String id) {
        return switch (id) {
            case "check_person/extremist_list" -> "Экстремистский список";
            case "check_person/executive_proceeding_base" -> "Исполнительные производства";
            case "check_person/fsin_wanted" -> "Розыск ФСИН";
            case "check_person/acc_stop" -> "Заблокированные счета";
            case "check_person/wanted_criminal" -> "Розыск МВД";
            case "check_person/expired_passport_v2" -> "Просроченный паспорт";
            case "check_person/credit_history" -> "Проблемы с кредитной историей";
            case "check_person/inoagent" -> "Иностранный агент";
            case "check_person/linkasso_manager_for" -> "Менеджер Linkasso";
            case "check_person/person_inn" -> "ИНН физического лица";

            default -> "Неизвестная проверка (" + id + ")";
        };
    }
}

