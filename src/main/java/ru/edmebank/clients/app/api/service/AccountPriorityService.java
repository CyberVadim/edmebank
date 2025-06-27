package ru.edmebank.clients.app.api.service;

import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с приоритетами счетов
 */
public interface AccountPriorityService {

    AccountPriorityDetailsResponse getAccountPriorityDetails(UUID accountId) throws ApiException;

    /**
     * Обновить настройки приоритетов счета
     *
     * @param accountId идентификатор счета
     * @param request запрос на обновление приоритетов
     * @return результат обновления приоритетов
     * @throws ApiException если счет не найден, не активен, доступ запрещен
     *                     или нарушены бизнес-правила (например, конфликт приоритетов)
     */
    AccountPriorityUpdateResponse updateAccountPriority(UUID accountId,
                                                        AccountPriorityUpdateRequest request) throws ApiException;

    /**
     * Получить историю изменений приоритетов счета
     *
     * @param accountId идентификатор счета
     * @return список изменений приоритетов в хронологическом порядке (новые в начале)
     * @throws ApiException если счет не найден или доступ запрещен
     */
    List<AccountPriorityDetailsResponse> getAccountPriorityHistory(UUID accountId) throws ApiException;

    /**
     * Проверить возможность установки приоритета для списания
     *
     * @param accountId идентификатор счета
     * @return true если приоритет можно установить, false в противном случае
     * @throws ApiException если счет не найден или доступ запрещен
     */
    boolean canSetWriteOffPriority(UUID accountId) throws ApiException;

    /**
     * Проверить возможность установки приоритета для начисления
     *
     * @param accountId идентификатор счета
     * @return true если приоритет можно установить, false в противном случае
     * @throws ApiException если счет не найден или доступ запрещен
     */
    boolean canSetAccrualPriority(UUID accountId) throws ApiException;
}