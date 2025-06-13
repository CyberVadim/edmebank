package ru.edmebank.accounts.adapter.input.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityGetResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityUpdateRequest;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.fw.exception.AccountException;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityController {

    private final AccountPriorityService accountPriorityService;

    @PutMapping("/{accountId}/priority")
    public ResponseEntity<AccountPriorityResponse> updatePriorities(
            @PathVariable String accountId,
            @Valid @RequestBody AccountPriorityUpdateRequest request,
            @RequestHeader("X-Request-ID") String requestId
    ) {
        log.info("Получен запрос на обновление приоритетов счета: {}. RequestID: {}", accountId, requestId);

        try {
            AccountPriorityResponse response = accountPriorityService.updatePriorities(accountId, request);
            return ResponseEntity.ok(response);
        } catch (AccountException e) {
            log.error("Ошибка при обновлении приоритетов счета: {}. Код ошибки: {}", e.getMessage(), e.getErrorCode(), e);

            AccountPriorityResponse errorResponse = AccountPriorityResponse.error(
                    e.getErrorCode(),
                    e.getMessage(),
                    e.getDetails()
            );

            return ResponseEntity.status(getHttpStatusByErrorCode(e.getErrorCode())).body(errorResponse);
        }
    }

    @GetMapping("/{accountId}/priority")
    public ResponseEntity<AccountPriorityGetResponse> getPriorities(
            @PathVariable String accountId,
            @RequestHeader("X-Request-ID") String requestId
    ) {
        log.info("Получен запрос на получение приоритетов счета: {}. RequestID: {}", accountId, requestId);

        try {
            AccountPriorityGetResponse response = accountPriorityService.getPriorities(accountId);
            return ResponseEntity.ok(response);
        } catch (AccountException e) {
            log.error("Ошибка при получении приоритетов счета: {}. Код ошибки: {}", e.getMessage(), e.getErrorCode(), e);

            AccountPriorityGetResponse errorResponse = AccountPriorityGetResponse.error(
                    e.getErrorCode(),
                    e.getMessage(),
                    e.getDetails()
            );

            return ResponseEntity.status(getHttpStatusByErrorCode(e.getErrorCode())).body(errorResponse);
        }
    }

    /**
     * Преобразует код ошибки в HTTP статус
     */
    private int getHttpStatusByErrorCode(String errorCode) {
        return switch (errorCode) {
            case "INVALID_REQUEST" -> 400;
            case "UNAUTHORIZED" -> 401;
            case "FORBIDDEN" -> 403;
            case "ACCOUNT_NOT_FOUND" -> 404;
            case "CONFLICT_PRIORITIES" -> 409;
            case "INVALID_ACCOUNT_STATE" -> 422;
            case "TOO_MANY_REQUESTS" -> 429;
            default -> 500;
        };
    }
}