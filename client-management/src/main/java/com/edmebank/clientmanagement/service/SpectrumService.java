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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpectrumService {
    private final SpectrumClient spectrumClient;

    @Value("${spectrum_data.authHeader}")
    private String authHeader;

    private static final Map<String, String> idCheckMap = new HashMap<>();

    static {
        idCheckMap.put("check_person/extremist_list", "Экстремистский список");
        idCheckMap.put("check_person/executive_proceeding_base", "Исполнительные производства");
        idCheckMap.put("check_person/fsin_wanted", "Розыск ФСИН");
        idCheckMap.put("check_person/acc_stop", "Заблокированные счета");
        idCheckMap.put("check_person/wanted_criminal", "Розыск МВД");
        idCheckMap.put("check_person/expired_passport_v2", "Просроченный паспорт");
        idCheckMap.put("check_person/credit_history", "Проблемы с кредитной историей");
        idCheckMap.put("check_person/inoagent", "Иностранный агент");
        idCheckMap.put("check_person/person_inn", "ИНН физического лица");
    }

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
            throw new ReportNotFoundException("Отчет для клиента: " + applicantData.getLastName() + " "
                    + applicantData.getFirstName() + " не найден");
        } else {
            return uid;
        }
    }

    public boolean canRegisterClient(ClientDTO clientDTO) {
        String uid = getUid(clientDTO);
        ResponseData response = spectrumClient.getReport(uid, false, false, authHeader);

        if (isEmpty(response.getData())) {
            return false;
        }

        ReportData report = response.getData().get(0);
        return processCheckResults(report.getState().getSources());
    }

    private boolean processCheckResults(List<SourceData> sources) {
        if (isEmpty(sources)) {
            return false;
        }
        StringBuilder resultMessage = new StringBuilder();
        boolean failed = false;

        for (SourceData source : sources) {
            String state = source.getState();
            String id = source.get_id();

            if (idCheckMap.containsKey(id)) {
                if (!"OK".equals(state)) {
                    Map<String, Object> data = source.getData();
                    if (data == null) {
                        resultMessage.append("Проверка: ").append(idCheckMap.getOrDefault(id, id)).append("\n")
                                .append("Ошибка: отсутствуют данные по источнику\n\n");
                        failed = true;
                        continue;
                    }

                    String reason = (String) data.get("reason");
                    resultMessage.append("state: ").append(state).append("\n")
                            .append("Проверка: ").append(idCheckMap.getOrDefault(id, id)).append("\n")
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
}


