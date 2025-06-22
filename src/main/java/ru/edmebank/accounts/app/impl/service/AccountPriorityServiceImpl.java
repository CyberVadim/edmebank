package ru.edmebank.accounts.app.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.domain.entity.Account;
import ru.edmebank.accounts.domain.enums.AccountStatus;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.AccountValidationException;
import ru.edmebank.accounts.fw.exception.TooManyRequestsException;
import ru.edmebank.accounts.adapter.output.repository.AccountRepository;
import ru.edmebank.accounts.utils.RateLimiter;
import ru.edmebank.contracts.dto.accounts.AccountPriorityData;
import ru.edmebank.contracts.dto.accounts.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityServiceImpl implements AccountPriorityService {

    private final AccountRepository accountRepository;
    private final RateLimiter rateLimiter;

    // In-memory кэш для хранения приоритетов
    private final Map<UUID, AccountPriorityData> priorityCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true) // только чтение из БД
    public AccountPriorityResponse updateAccountPriority(UUID accountId, AccountPriorityRequest request, UUID requestId) {
        log.info("Setting account priority for account: {}, requestId: {}", accountId, requestId);

        // Применение ограничения скорости запросов
        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException("Превышен лимит запросов на изменение приоритетов");
        }

        // Получение счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Счет не найден с идентификатором: " + accountId));

        // Валидация состояния счета
        validateAccountForPriorityChange(account);

        // Валидация бизнес-правил
        validatePriorityRules(account, request);

        // Получение или создание данных о приоритетах
        AccountPriorityData priorityData = getPriorityData(accountId).orElse(new AccountPriorityData());

        // Обновление версии
        priorityData.setVersion(priorityData.getVersion() + 1);

        // Обновление приоритетов
        priorityData.setPriorityForWriteOff(request.getPriorityForWriteOff());
        priorityData.setPriorityForAccrual(request.getPriorityForAccrual());

        // Сохранение информации об инициаторе
        if (request.getInitiator() != null) {
            priorityData.setLastUpdatedBy(request.getInitiator().getName());
            priorityData.setLastUpdatedRole(request.getInitiator().getRole());
        }
        priorityData.setLastUpdateReason(request.getReason());
        priorityData.setAccountId(accountId);
        priorityData.setUpdatedAt(LocalDateTime.now());

        // Сохранение в кэше
        priorityCache.put(accountId, priorityData);

        // Запись в аудит-лог
        logPriorityChange(account, priorityData, request);

        // Формирование ответа
        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("SUCCESS");

        AccountPriorityResponse.PriorityData data = new AccountPriorityResponse.PriorityData();
        data.setAccountId(account.getAccountId().toString());

        AccountPriorityResponse.UpdatedPriorities updatedPriorities = new AccountPriorityResponse.UpdatedPriorities();
        updatedPriorities.setPriorityForWriteOff(priorityData.getPriorityForWriteOff());
        updatedPriorities.setPriorityForAccrual(priorityData.getPriorityForAccrual());

        data.setUpdatedPriorities(updatedPriorities);
        data.setUpdatedAt(priorityData.getUpdatedAt().atZone(ZoneId.systemDefault()));
        data.setVersion(priorityData.getVersion());

        response.setData(data);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityDetailsResponse getAccountPriority(UUID accountId, UUID requestId) {
        log.info("Getting account priority for account: {}, requestId: {}", accountId, requestId);

        // Получение счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Счет не найден с идентификатором: " + accountId));

        // Получение данных о приоритетах из кэша
        Optional<AccountPriorityData> priorityDataOpt = getPriorityData(accountId);

        // Формирование ответа
        AccountPriorityDetailsResponse response = new AccountPriorityDetailsResponse();
        response.setStatus("SUCCESS");

        AccountPriorityDetailsResponse.PriorityData data = new AccountPriorityDetailsResponse.PriorityData();
        data.setAccountId(account.getAccountId().toString());
        data.setAccountType(account.getAccountType());

        AccountPriorityDetailsResponse.CurrentPriorities currentPriorities = new AccountPriorityDetailsResponse.CurrentPriorities();
        if (priorityDataOpt.isPresent()) {
            AccountPriorityData priorityData = priorityDataOpt.get();
            currentPriorities.setPriorityForWriteOff(priorityData.getPriorityForWriteOff());
            currentPriorities.setPriorityForAccrual(priorityData.getPriorityForAccrual());
        } else {
            currentPriorities.setPriorityForWriteOff(false);
            currentPriorities.setPriorityForAccrual(false);
        }
        data.setCurrentPriorities(currentPriorities);

        AccountPriorityDetailsResponse.AllowedChanges allowedChanges = new AccountPriorityDetailsResponse.AllowedChanges();
        allowedChanges.setCanSetWriteOff(canSetWriteOffPriority(account));
        allowedChanges.setCanSetAccrual(canSetAccrualPriority(account));
        allowedChanges.setReasons(getRestrictionReasons(account));
        data.setAllowedChanges(allowedChanges);

        AccountPriorityDetailsResponse.LastUpdated lastUpdated = new AccountPriorityDetailsResponse.LastUpdated();
        if (priorityDataOpt.isPresent()) {
            AccountPriorityData priorityData = priorityDataOpt.get();
            lastUpdated.setDate(priorityData.getUpdatedAt().atZone(ZoneId.systemDefault()));
            String updatedBy = priorityData.getLastUpdatedBy();
            String updatedRole = priorityData.getLastUpdatedRole();
            if (updatedBy != null && updatedRole != null) {
                lastUpdated.setBy(updatedBy + " (" + updatedRole + ")");
            } else {
                lastUpdated.setBy("Система");
            }
        } else {
            lastUpdated.setDate(account.getUpdatedAt().atZone(ZoneId.systemDefault()));
            lastUpdated.setBy("Система");
        }
        data.setLastUpdated(lastUpdated);

        response.setData(data);

        return response;
    }

    private Optional<AccountPriorityData> getPriorityData(UUID accountId) {
        return Optional.ofNullable(priorityCache.get(accountId));
    }

    private void validateAccountForPriorityChange(Account account) {
        // Проверка закрытого счета
        if (AccountStatus.CLOSED.toString().equalsIgnoreCase(account.getStatus())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно изменить приоритеты для закрытого счета");
        }

        // Проверка заблокированного счета
        if (AccountStatus.BLOCKED.toString().equalsIgnoreCase(account.getStatus())) {
            Map<String, Object> details = new HashMap<>();
            details.put("accountId", account.getAccountId().toString());
            details.put("blockReason", "OVERDUE_PAYMENT");
            details.put("blockDate", ZonedDateTime.now().minusDays(7));

            throw new AccountValidationException("ACCOUNT_BLOCKED",
                    "Невозможно изменить приоритеты для заблокированного счета", details);
        }

        // Проверка замороженного счета
        if (AccountStatus.FROZEN.toString().equalsIgnoreCase(account.getStatus())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно изменить приоритеты для замороженного счета");
        }
    }

    private void validatePriorityRules(Account account, AccountPriorityRequest request) {
        // Проверка конфликта приоритетов для кредитных счетов
        if (account.getAccountType() != null &&
            account.getAccountType().startsWith("LOAN_") &&
            Boolean.TRUE.equals(request.getPriorityForWriteOff()) &&
            Boolean.TRUE.equals(request.getPriorityForAccrual())) {
            throw new AccountValidationException("CONFLICT_PRIORITIES",
                    "Для кредитных счетов можно установить только один тип приоритета");
        }

        // Проверка для депозитных счетов
        if (account.getAccountType() != null &&
            ("DEPOSIT".equalsIgnoreCase(account.getAccountType()) ||
             "сберегательный".equalsIgnoreCase(account.getAccountType()))) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Для депозитных счетов приоритеты не могут быть изменены");
        }

        // Проверки для счетов с отрицательным балансом
        if (account.getBalance() != null && account.getBalance().signum() < 0 &&
            Boolean.TRUE.equals(request.getPriorityForWriteOff())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритет списания для счета с отрицательным балансом");
        }

        // Проверка счетов с просроченной задолженностью
        if (hasOverdueDebt(account) && Boolean.TRUE.equals(request.getPriorityForWriteOff())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритет списания для счета с просроченной задолженностью");
        }

        // Проверка счетов с истекшим сроком действия
        if (hasExpiredTerm(account) && Boolean.TRUE.equals(request.getPriorityForAccrual())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритет начисления для счета с истекшим сроком действия");
        }

        // Проверка счетов с ограниченными операциями
        if (hasLimitedOperations(account) && Boolean.TRUE.equals(request.getPriorityForAccrual())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритет начисления для счета с ограниченными операциями");
        }

        // Проверка счетов, привязанных к коллекторскому взысканию
        if (isLinkedToCollection(account) && Boolean.TRUE.equals(request.getPriorityForAccrual())) {
            throw new AccountValidationException("INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритет начисления для счета, привязанного к коллекторскому взысканию");
        }
    }

    private boolean canSetWriteOffPriority(Account account) {
        // Проверка статуса счета
        if (!AccountStatus.ACTIVE.toString().equalsIgnoreCase(account.getStatus())) {
            return false;
        }

        // Проверка баланса
        if (account.getBalance() != null && account.getBalance().signum() < 0) {
            return false;
        }

        // Проверка просроченной задолженности
        if (hasOverdueDebt(account)) {
            return false;
        }

        return true;
    }

    private boolean canSetAccrualPriority(Account account) {
        // Проверка статуса счета
        if (!AccountStatus.ACTIVE.toString().equalsIgnoreCase(account.getStatus())) {
            return false;
        }

        // Проверка типа счета
        if (account.getAccountType() != null &&
            ("DEPOSIT".equalsIgnoreCase(account.getAccountType()) ||
             "сберегательный".equalsIgnoreCase(account.getAccountType()))) {
            return false;
        }

        // Проверка истекшего срока действия
        if (hasExpiredTerm(account)) {
            return false;
        }

        // Проверка ограниченных операций
        if (hasLimitedOperations(account)) {
            return false;
        }

        // Проверка привязки к коллекторскому взысканию
        if (isLinkedToCollection(account)) {
            return false;
        }

        return true;
    }

    private List<String> getRestrictionReasons(Account account) {
        List<String> reasons = new ArrayList<>();

        // Добавление причин ограничений в зависимости от типа и состояния счета
        if (account.getAccountType() != null && account.getAccountType().startsWith("LOAN_")) {
            reasons.add("Только один приоритет разрешен для кредитных счетов");
        }

        if (account.getAccountType() != null &&
            ("DEPOSIT".equalsIgnoreCase(account.getAccountType()) ||
             "сберегательный".equalsIgnoreCase(account.getAccountType()))) {
            reasons.add("Для депозитных счетов приоритеты не могут быть изменены");
        }

        if (AccountStatus.CLOSED.toString().equalsIgnoreCase(account.getStatus())) {
            reasons.add("Невозможно изменить приоритеты для закрытого счета");
        }

        if (AccountStatus.BLOCKED.toString().equalsIgnoreCase(account.getStatus())) {
            reasons.add("Невозможно изменить приоритеты для заблокированного счета");
        }

        if (AccountStatus.FROZEN.toString().equalsIgnoreCase(account.getStatus())) {
            reasons.add("Невозможно изменить приоритеты для замороженного счета");
        }

        if (account.getBalance() != null && account.getBalance().signum() < 0) {
            reasons.add("Невозможно установить приоритет списания для счета с отрицательным балансом");
        }

        if (hasOverdueDebt(account)) {
            reasons.add("Невозможно установить приоритет списания для счета с просроченной задолженностью");
        }

        if (hasExpiredTerm(account)) {
            reasons.add("Невозможно установить приоритет начисления для счета с истекшим сроком действия");
        }

        if (hasLimitedOperations(account)) {
            reasons.add("Невозможно установить приоритет начисления для счета с ограниченными операциями");
        }

        if (isLinkedToCollection(account)) {
            reasons.add("Невозможно установить приоритет начисления для счета, привязанного к коллекторскому взысканию");
        }

        return reasons;
    }

    private boolean hasOverdueDebt(Account account) {
        // Здесь будет реальная логика проверки просроченной задолженности
        // Для примера возвращаем false
        return false;
    }

    private boolean hasExpiredTerm(Account account) {
        // Здесь будет реальная логика проверки истекшего срока действия
        // Для примера проверяем, что счет открыт более 5 лет назад
        return account.getOpenedAt() != null &&
               account.getOpenedAt().plusYears(5).isBefore(LocalDateTime.now());
    }

    private boolean hasLimitedOperations(Account account) {
        // Здесь будет реальная логика проверки ограниченных операций
        // Для примера возвращаем false
        return false;
    }

    private boolean isLinkedToCollection(Account account) {
        // Здесь будет реальная логика проверки привязки к коллекторскому взысканию
        // Для примера возвращаем false
        return false;
    }

    private void logPriorityChange(Account account, AccountPriorityData priorityData, AccountPriorityRequest request) {
        // Логирование изменений
        log.info("Priority change for account {}: writeOff={}, accrual={}, reason={}, initiator={}",
                account.getAccountId(),
                priorityData.getPriorityForWriteOff(),
                priorityData.getPriorityForAccrual(),
                request.getReason(),
                request.getInitiator() != null ?
                        request.getInitiator().getName() + " (" + request.getInitiator().getRole() + ")" : "N/A");
    }
}