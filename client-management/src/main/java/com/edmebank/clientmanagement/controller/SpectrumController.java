package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.spectrum.getReport.ReportData;
import com.edmebank.clientmanagement.service.SpectrumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/spectrum")
@RequiredArgsConstructor
public class SpectrumController {
    private final SpectrumService spectrumService;

    @PostMapping("/{clientId}/get-uid")
    public String checkApplicant(@PathVariable UUID clientId) {
        return spectrumService.getUid(clientId);
    }

    @GetMapping("/{uid}")
    public ReportData fetchReport(@PathVariable String uid) {
        return spectrumService.fetchReport(uid);
    }

    @GetMapping("/check/{uid}")
    public ResponseEntity<String> checkClientRegistration(@PathVariable String uid) {
        boolean canRegister = spectrumService.canRegisterClient(uid);
        return canRegister
                ? ResponseEntity.ok("Клиент может быть зарегистрирован")
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Клиенту отказано в регистрации");
    }
}

