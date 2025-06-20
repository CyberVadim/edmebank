package ru.edmebank.accounts.adapter.input.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest;

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
        AccountPriorityResponse response = accountPriorityService.updatePriorities(accountId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/priority")
    public ResponseEntity<AccountPriorityGetResponse> getPriorities(
            @PathVariable String accountId,
            @RequestHeader("X-Request-ID") String requestId
    ) {
        log.info("Получен запрос на получение приоритетов счета: {}. RequestID: {}", accountId, requestId);
        AccountPriorityGetResponse response = accountPriorityService.getPriorities(accountId);
        return ResponseEntity.ok(response);
    }
}