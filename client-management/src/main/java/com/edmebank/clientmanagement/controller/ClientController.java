package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.dto.PassportDto;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ReportData;
import com.edmebank.clientmanagement.service.ClientService;
import com.edmebank.clientmanagement.service.PassportService;
import com.edmebank.clientmanagement.service.SpectrumService;
import com.edmebank.clientmanagement.service.NotificationService;
import com.edmebank.clientmanagement.service.impl.PassportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final PassportServiceImpl passportService;

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


    @PutMapping(value = "/{clientId}/update",
                headers = {
                    "Content-Type=application/json",  // Ожидается JSON
                    "SystemId",                     // Должен присутствовать (любое значение)
                    "Content-Length"                 // Должен присутствовать
                })
    public ResponseEntity<Void> updateClient(@PathVariable UUID clientId,
                                             @Valid @RequestBody ClientDTO clientDTO) {
        clientService.updateClient(clientId, clientDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{clientId}/patch",
            headers = {
                    "Content-Type=application/json",  // Ожидается JSON
                    "SystemId",                     // Должен присутствовать (любое значение)
                    "Content-Length"                 // Должен присутствовать
            })
    public ResponseEntity<Void> patchClient(@PathVariable UUID clientId,
                                            @Valid @RequestBody ClientDTO clientDTO) {
        clientService.patchClient(clientId, clientDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            path = "/{clientId}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PassportDto> uploadDocuments(@PathVariable UUID clientId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(passportService.uploadPassport(clientId, file));
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