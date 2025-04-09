package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.DadataFeignClient;
import com.edmebank.clientmanagement.dto.dadata.DadataPassportResponse;
import com.edmebank.clientmanagement.exception.InvalidPassportException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PassportValidationService {

    private final DadataFeignClient dadataFeignClient;

    @Value("${dadata.api.authHeader}")
    private String authHeader;

    @Value("${dadata.api.secret}")
    private String secret;

    public boolean isValid(String passportNumber) {
        List<DadataPassportResponse> response = dadataFeignClient.cleanPassport(
                Collections.singletonList(passportNumber),
                authHeader,
                secret);

        if (response.isEmpty()) {
            return false;
        }

        int qc = response.get(0).getQc();

        switch (qc) {
            case 1 -> throw new InvalidPassportException("Неправильный формат серии или номера паспорта");
            case 10 -> throw new InvalidPassportException("Паспорт числится недействительным в базе МВД");
            case 0 -> { return true; }
            default -> { return false; }
        }
    }
}
