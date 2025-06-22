package ru.edmebank.accounts.app.api.service;

import ru.edmebank.contracts.dto.accounts.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.util.UUID;

public interface AccountPriorityService {
    AccountPriorityResponse updateAccountPriority(UUID accountId, AccountPriorityRequest request, UUID requestId);
    AccountPriorityDetailsResponse getAccountPriority(UUID accountId, UUID requestId);
}