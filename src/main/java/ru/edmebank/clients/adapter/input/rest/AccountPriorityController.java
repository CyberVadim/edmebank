package ru.edmebank.clients.adapter.input.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.clients.fw.exception.ErrorResponse;
import ru.edmebank.contracts.dto.common.ApiResponseDto;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Priorities", description = "API для управления приоритетами счетов")
public class AccountPriorityController {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String AUTH_HEADER = "Authorization";

    private final AccountPriorityService accountPriorityService;

    @GetMapping("/{accountId}/priority")
    @Operation(summary = "Получение текущих настроек приоритетов счета",
            description = "Возвращает текущие настройки приоритетов счета и доступные изменения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение настроек",
                    content = @Content(schema = @Schema(implementation = AccountPriorityDetailsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Отсутствует или невалидный токен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Счет не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Недопустимое состояние счета",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponseDto<AccountPriorityDetailsResponse>> getAccountPriority(
            @PathVariable @Parameter(description = "Идентификатор счета") UUID accountId,
            @RequestHeader(value = REQUEST_ID_HEADER, required = false)
            @Parameter(description = "Идентификатор запроса") String requestId,
            @RequestHeader(value = AUTH_HEADER, required = false)
            @Parameter(description = "JWT токен в формате 'Bearer {token}'") String authHeader) {

        log.info("Получение приоритетов счета: {}, RequestId: {}", accountId, requestId);
        AccountPriorityDetailsResponse details = accountPriorityService.getAccountPriorityDetails(accountId, authHeader);
        return ResponseEntity.ok(ApiResponseDto.success(details));
    }

    @PutMapping("/{accountId}/priority")
    @Operation(summary = "Обновление настроек приоритетов счета",
            description = "Устанавливает новые значения приоритетов для списания и начисления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обновление настроек",
                    content = @Content(schema = @Schema(implementation = AccountPriorityUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Отсутствует или невалидный токен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Несоответствие инициатора и токена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Счет не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Конфликт приоритетов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Недопустимое состояние счета",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponseDto<AccountPriorityUpdateResponse>> updateAccountPriority(
            @PathVariable @Parameter(description = "Идентификатор счета") UUID accountId,
            @Valid @RequestBody @Parameter(description = "Параметры обновления приоритетов")
            AccountPriorityUpdateRequest request,
            @RequestHeader(value = REQUEST_ID_HEADER, required = false)
            @Parameter(description = "Идентификатор запроса") String requestId,
            @RequestHeader(value = AUTH_HEADER, required = false)
            @Parameter(description = "JWT токен в формате 'Bearer {token}'") String authHeader) {

        log.info("Обновление приоритетов счета: {}, RequestId: {}. WriteOff: {}, Accrual: {}, Initiator: {} ({})",
                accountId, requestId, request.getPriorityForWriteOff(), request.getPriorityForAccrual(),
                request.getInitiator().getName(), request.getInitiator().getRole());

        AccountPriorityUpdateResponse result = accountPriorityService.updateAccountPriority(accountId, request, authHeader);
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }
}