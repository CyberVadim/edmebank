package ru.edmebank.accounts.app.api.service;

import ru.edmebank.accounts.fw.exception.AccountException;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest;

public interface AccountPriorityService {

    AccountPriorityResponse updatePriorities(String accountId, AccountPriorityUpdateRequest request) throws AccountException;

    AccountPriorityGetResponse getPriorities(String accountId) throws AccountException;
}