package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.SpectrumClient;
import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.dto.spectrum.ApplicantData;
import com.edmebank.clientmanagement.dto.spectrum.ApplicantRequest;
import com.edmebank.clientmanagement.dto.spectrum.checkUid.ResponseUidData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ReportData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ResponseData;
import com.edmebank.clientmanagement.dto.spectrum.getReport.SourceData;
import com.edmebank.clientmanagement.exception.ReportNotFoundException;
import com.edmebank.clientmanagement.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpectrumService {
    private final SpectrumClient spectrumClient;
    @Value("${spectrum_data.authHeader}")
    private String authHeader;
    private final ClientRepository clientRepository;
    private final List<String> idCheckList = List.of(
            "check_person/extremist_list",
            "check_person/executive_proceeding_base",
            "check_person/fsin_wanted",
            "check_person/acc_stop",
            "check_person/wanted_criminal",
            "check_person/expired_passport_v2",
            "check_person/credit_history",
            "check_person/inoagent",
            "check_person/person_inn"
    );

    public String getUid(ClientDTO clientDTO) {

        ApplicantData applicantData = ApplicantData.builder()
                .lastName(clientDTO.getLastName())
                .firstName(clientDTO.getFirstName())
                .patronymic(clientDTO.getMiddleName())
                .birth(clientDTO.getDateOfBirth().toString())
                .passport(clientDTO.getPassportNumber())
                .passportDate(clientDTO.getPassportIssueDate().toString())
                .phone(clientDTO.getPhone())
                .inn(clientDTO.getInn())
                .build();


        ApplicantRequest request = new ApplicantRequest("MULTIPART", " ", applicantData);
        ResponseUidData responseUidData = spectrumClient.checkApplicant(authHeader, request);
        String uid = responseUidData.getData().get(0).getUid();

        if (uid == null) {
            throw new ReportNotFoundException("Отчет для клиента: %s %s не найден", clientDTO.getLastName(), clientDTO.getFirstName());
        } else {
            return uid;
        }
    }

    public Boolean canRegisterClient(ClientDTO clientDTO) {
        String uid = getUid(clientDTO);
        ResponseData response = spectrumClient.getReport(uid, false, false, authHeader);

        if (isEmpty(response.getData())) {
            return false;
        }

        ReportData report = response.getData().get(0);
        return processCheckResults(report.getState().getSources());
    }


    private Boolean processCheckResults(List<SourceData> sources) {
        if (isEmpty(sources)) {
            return false;
        }
        StringBuilder resultMessage = new StringBuilder();
        boolean failed = false;
        for (SourceData source : sources) {
            String state = source.getState();
            String id = source.get_id();


            if (idCheckList.contains(id)) {
                if (!"OK".equals(state)) {
                    String reason = (String) source.getData().get("reason");
                    resultMessage.append("state: ").append(state).append("\n")
                            .append("Проверка: ").append(getCheckDescription(id)).append("\n")
                            .append("Ответ: ").append(reason).append("\n\n");
                    failed = true;
                }
            }
        }
        if (failed) {
            log.info(resultMessage.toString());
            return false;
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
            case "check_person/person_inn" -> "ИНН физического лица";

            default -> "Неизвестная проверка (" + id + ")";
        };
    }
}

