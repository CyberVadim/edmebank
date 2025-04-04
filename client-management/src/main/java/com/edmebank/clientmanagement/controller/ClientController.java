package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ReportData;
import com.edmebank.clientmanagement.service.ClientService;
import com.edmebank.clientmanagement.service.SpectrumService;
import com.edmebank.clientmanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final SpectrumService spectrumService;
    private final NotificationService notificationService;

    @Operation(summary = "Регистрация клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент успешно зарегистрирован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UUID.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "Неверный формат email"))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "Недостаточно прав доступа"))),
            @ApiResponse(responseCode = "404", description = "Не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "Клиент не найден"))),
            @ApiResponse(responseCode = "409", description = "Конфликт данных",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Клиент с таким email уже существует\" }")))
    })
    @PostMapping("/register")
    public ResponseEntity<UUID> registerClient(@Valid @RequestBody ClientDTO clientDTO) {
        UUID clientId = clientService.registerClient(clientDTO);
        return ResponseEntity.ok(clientId);
    }


    @PutMapping("/{clientId}/update")
    public ResponseEntity<Void> updateClient(@PathVariable UUID clientId, @Valid @RequestBody ClientDTO clientDTO) {
        clientService.updateClient(clientId, clientDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clientId}/documents")
    public ResponseEntity<String> uploadDocuments(@PathVariable UUID clientId, @RequestParam("files") List<MultipartFile> files) {
        try {
            clientService.uploadDocuments(clientId, files);
            return ResponseEntity.ok("Документы загружены");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка загрузки документов");
        }
    }

    @GetMapping("/report")
    public ReportData fetchReport(@Valid @RequestBody ClientDTO clientDTO) {
        return spectrumService.fetchReport(clientDTO);
    }

    @GetMapping("/{clientId}/disableNotification")
    public ResponseEntity<String> disableNotification(@PathVariable UUID clientId) {
        clientService.disableNotification(clientId);
        notificationService.disableNotification(clientId);
        return ResponseEntity.ok("Вы отписались от уведомлений");
    }
}