package ru.edmebank.accounts.app.api.service;

import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityGetResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityUpdateRequest;

public interface AccountPriorityService {

    /**
     * Обновляет приоритеты счета для списания и начисления средств
     * @param accountId ID счета
     * @param request Параметры запроса
     * @return Результат обновления приоритетов
     */
    AccountPriorityResponse updatePriorities(String accountId, AccountPriorityUpdateRequest request);

    /**
     * Получает текущие приоритеты счета и доступные изменения
     * @param accountId ID счета
     * @return Информация о приоритетах счета
     */
    AccountPriorityGetResponse getPriorities(String accountId);
}