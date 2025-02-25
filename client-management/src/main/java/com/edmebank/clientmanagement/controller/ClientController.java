package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<UUID> registerClient(@Valid @RequestBody ClientDTO clientDTO) {
        UUID clientId = clientService.registerClient(clientDTO);
        return ResponseEntity.ok(clientId);
    }

    @PutMapping("/{clientId}/update")
    public ResponseEntity<Void> updateClient(@PathVariable UUID clientId,@Valid @RequestBody ClientDTO clientDTO) {
        clientService.updateClient(clientId, clientDTO);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/{clientId}/documents")
//    public ResponseEntity<String> uploadDocuments(@PathVariable UUID clientId, @RequestBody ClientDocumentRequest request) {
//        // Логика загрузки документов
//        return ResponseEntity.ok("Документы загружены");
//    }
//
//    @GetMapping("/{clientId}/aml-check")
//    public ResponseEntity<String> checkClientAML(@PathVariable UUID clientId) {
//        // Логика AML/KYC проверки
//        return ResponseEntity.ok("AML/KYC проверка завершена");
//    }
//
//    @PostMapping("/{clientId}/products")
//    public ResponseEntity<String> linkProductToClient(@PathVariable UUID clientId, @RequestBody ClientProductRequest request) {
//        // Логика привязки банковского продукта
//        return ResponseEntity.ok("Продукт привязан к клиенту");
//    }
//
//    @PostMapping("/{clientId}/notifications")
//    public ResponseEntity<String> sendNotification(@PathVariable UUID clientId, @RequestBody NotificationRequest request) {
//        // Логика отправки уведомлений
//        return ResponseEntity.ok("Уведомление отправлено");
//    }
}

