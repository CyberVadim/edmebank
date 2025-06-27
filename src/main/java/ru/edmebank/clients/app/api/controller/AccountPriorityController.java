package ru.edmebank.clients.app.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления приоритетами счетов
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityController {

    private final AccountPriorityService accountPriorityService;

    /**
     * Получение текущих настроек приоритетов счета
     * GET /api/accounts/{accountId}/priority
     */
    @GetMapping("/{accountId}/priority")
    public ResponseEntity<ApiResponse<AccountPriorityDetailsResponse>> getAccountPriority(
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("Получение приоритетов счета: {}, requestId: {}, user: {}",
                accountId, requestId, "CyberVadim");

        AccountPriorityDetailsResponse details = accountPriorityService.getAccountPriorityDetails(accountId);
        return ResponseEntity.ok(ApiResponse.success(details));
    }

    /**
     * Обновление настроек приоритетов счета
     * PUT /api/accounts/{accountId}/priority
     */
    @PutMapping("/{accountId}/priority")
    public ResponseEntity<ApiResponse<AccountPriorityUpdateResponse>> updateAccountPriority(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountPriorityUpdateRequest request,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("Обновление приоритетов счета: {}, requestId: {}, user: {}, writeOff: {}, accrual: {}",
                accountId, requestId, "CyberVadim",
                request.getPriorityForWriteOff(), request.getPriorityForAccrual());

        AccountPriorityUpdateResponse result = accountPriorityService.updateAccountPriority(accountId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Получение истории изменений приоритетов счета
     * GET /api/accounts/{accountId}/priority/history
     */
    @GetMapping("/{accountId}/priority/history")
    public ResponseEntity<ApiResponse<List<AccountPriorityDetailsResponse>>> getAccountPriorityHistory(
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("Получение истории приоритетов счета: {}, requestId: {}, user: {}",
                accountId, requestId, "CyberVadim");

        List<AccountPriorityDetailsResponse> history = accountPriorityService.getAccountPriorityHistory(accountId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}