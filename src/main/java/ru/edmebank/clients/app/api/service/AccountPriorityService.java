package ru.edmebank.clients.app.api.service;

import ru.edmebank.clients.fw.exception.AccountPriorityException;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;

import java.util.UUID;

public interface AccountPriorityService {

    /**
     * Получить текущие настройки приоритетов счета
     */
    AccountPriorityDetailsResponse getAccountPriorityDetails(UUID accountId) throws AccountPriorityException;

    /**
     * Обновить настройки приоритетов счета
     */
    AccountPriorityUpdateResponse updateAccountPriority(
            UUID accountId, AccountPriorityUpdateRequest request) throws AccountPriorityException;

}