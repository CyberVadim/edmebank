package ru.edmebank.accounts.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.contracts.dto.accounts.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountPriorityController {

    private final AccountPriorityService accountPriorityService;

    @PutMapping("/{accountId}/priority")
    @PreAuthorize("hasAnyRole('CREDIT_MANAGER', 'ADMIN')")
    public ResponseEntity<AccountPriorityResponse> updateAccountPriority(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountPriorityRequest request,
            @RequestHeader("X-Request-ID") UUID requestId) {
        log.info("Received request to update account priority: accountId={}, requestId={}", accountId, requestId);
        return ResponseEntity.ok(accountPriorityService.updateAccountPriority(accountId, request, requestId));
    }

    @GetMapping("/{accountId}/priority")
    @PreAuthorize("hasAnyRole('CREDIT_MANAGER', 'ADMIN', 'CLIENT')")
    public ResponseEntity<AccountPriorityDetailsResponse> getAccountPriority(
            @PathVariable UUID accountId,
            @RequestHeader("X-Request-ID") UUID requestId) {
        log.info("Received request to get account priority: accountId={}, requestId={}", accountId, requestId);
        return ResponseEntity.ok(accountPriorityService.getAccountPriority(accountId, requestId));
    }
}