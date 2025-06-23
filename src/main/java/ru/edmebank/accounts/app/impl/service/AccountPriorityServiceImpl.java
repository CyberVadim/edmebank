package ru.edmebank.accounts.app.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.accounts.adapter.output.repository.AccountRepository;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.domain.entity.Account;
import ru.edmebank.accounts.domain.enums.AccountStatus;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.AccountValidationException;
import ru.edmebank.accounts.fw.exception.TooManyRequestsException;
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

    // Константы
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String SYSTEM_USER = "Система";
    private static final String ACCOUNT_NOT_FOUND_MSG = "Счет не найден с идентификатором: ";
    private static final String INVALID_ACCOUNT_STATE = "INVALID_ACCOUNT_STATE";
    private static final String ACCOUNT_BLOCKED = "ACCOUNT_BLOCKED";
    private static final String CONFLICT_PRIORITIES = "CONFLICT_PRIORITIES";

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityResponse updateAccountPriority(UUID accountId, AccountPriorityRequest request, UUID requestId) {
        log.info("Setting account priority for account: {}, requestId: {}", accountId, requestId);

        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException("Превышен лимит запросов на изменение приоритетов");
        }

        Account account = getAccountById(accountId);
        validateAccountForPriorityChange(account);
        validatePriorityRules(account, request);

        AccountPriorityData priorityData = updatePriorityData(accountId, request);
        logPriorityChange(account, priorityData, request);

        return createSuccessResponse(account, priorityData);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityDetailsResponse getAccountPriority(UUID accountId, UUID requestId) {
        log.info("Getting account priority for account: {}, requestId: {}", accountId, requestId);

        Account account = getAccountById(accountId);
        Optional<AccountPriorityData> priorityDataOpt = Optional.ofNullable(priorityCache.get(accountId));

        return createDetailsResponse(account, priorityDataOpt);
    }

    // --- Вспомогательные методы ---

    private Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG + accountId));
    }

    private AccountPriorityData updatePriorityData(UUID accountId, AccountPriorityRequest request) {
        AccountPriorityData priorityData = priorityCache.getOrDefault(accountId, new AccountPriorityData());

        priorityData.setVersion(priorityData.getVersion() + 1);
        priorityData.setPriorityForWriteOff(request.getPriorityForWriteOff());
        priorityData.setPriorityForAccrual(request.getPriorityForAccrual());

        if (request.getInitiator() != null) {
            priorityData.setLastUpdatedBy(request.getInitiator().getName());
            priorityData.setLastUpdatedRole(request.getInitiator().getRole());
        }

        priorityData.setLastUpdateReason(request.getReason());
        priorityData.setAccountId(accountId);
        priorityData.setUpdatedAt(ZonedDateTime.now());

        priorityCache.put(accountId, priorityData);

        return priorityData;
    }

    private AccountPriorityResponse createSuccessResponse(Account account, AccountPriorityData priorityData) {
        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus(SUCCESS_STATUS);

        AccountPriorityResponse.PriorityData data = new AccountPriorityResponse.PriorityData();
        data.setAccountId(account.getAccountId().toString());

        AccountPriorityResponse.UpdatedPriorities updatedPriorities = new AccountPriorityResponse.UpdatedPriorities();
        updatedPriorities.setPriorityForWriteOff(priorityData.getPriorityForWriteOff());
        updatedPriorities.setPriorityForAccrual(priorityData.getPriorityForAccrual());

        data.setUpdatedPriorities(updatedPriorities);

        // Исправленная версия конвертации LocalDateTime в ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                priorityData.getUpdatedAt().toLocalDate(),
                priorityData.getUpdatedAt().toLocalTime(),
                ZoneId.systemDefault()
        );
        data.setUpdatedAt(zonedDateTime);

        data.setVersion(priorityData.getVersion());

        response.setData(data);

        return response;
    }

    private AccountPriorityDetailsResponse createDetailsResponse(Account account, Optional<AccountPriorityData> priorityDataOpt) {
        AccountPriorityDetailsResponse response = new AccountPriorityDetailsResponse();
        response.setStatus(SUCCESS_STATUS);

        AccountPriorityDetailsResponse.PriorityData data = new AccountPriorityDetailsResponse.PriorityData();
        data.setAccountId(account.getAccountId().toString());
        data.setAccountType(account.getAccountType());

        // Настройка текущих приоритетов
        AccountPriorityDetailsResponse.CurrentPriorities currentPriorities = new AccountPriorityDetailsResponse.CurrentPriorities();
        priorityDataOpt.ifPresent(priorityData -> {
            currentPriorities.setPriorityForWriteOff(priorityData.getPriorityForWriteOff());
            currentPriorities.setPriorityForAccrual(priorityData.getPriorityForAccrual());
        });

        if (!priorityDataOpt.isPresent()) {
            currentPriorities.setPriorityForWriteOff(false);
            currentPriorities.setPriorityForAccrual(false);
        }

        data.setCurrentPriorities(currentPriorities);

        // Настройка разрешенных изменений
        AccountPriorityDetailsResponse.AllowedChanges allowedChanges = new AccountPriorityDetailsResponse.AllowedChanges();
        allowedChanges.setCanSetWriteOff(canSetWriteOffPriority(account));
        allowedChanges.setCanSetAccrual(canSetAccrualPriority(account));
        allowedChanges.setReasons(getRestrictionReasons(account));
        data.setAllowedChanges(allowedChanges);

        // Настройка информации о последнем обновлении
        AccountPriorityDetailsResponse.LastUpdated lastUpdated = new AccountPriorityDetailsResponse.LastUpdated();

        if (priorityDataOpt.isPresent()) {
            AccountPriorityData priorityData = priorityDataOpt.get();

            // Исправленная версия конвертации LocalDateTime в ZonedDateTime
            ZonedDateTime zonedDateTime = ZonedDateTime.of(
                    priorityData.getUpdatedAt().toLocalDate(),
                    priorityData.getUpdatedAt().toLocalTime(),
                    ZoneId.systemDefault()
            );
            lastUpdated.setDate(zonedDateTime);

            String updatedBy = priorityData.getLastUpdatedBy();
            String updatedRole = priorityData.getLastUpdatedRole();
            lastUpdated.setBy((updatedBy != null && updatedRole != null) ?
                    updatedBy + " (" + updatedRole + ")" : SYSTEM_USER);
        } else {
            // Исправленная версия конвертации LocalDateTime в ZonedDateTime
            ZonedDateTime zonedDateTime = ZonedDateTime.of(
                    account.getUpdatedAt().toLocalDate(),
                    account.getUpdatedAt().toLocalTime(),
                    ZoneId.systemDefault()
            );
            lastUpdated.setDate(zonedDateTime);
            lastUpdated.setBy(SYSTEM_USER);
        }

        data.setLastUpdated(lastUpdated);
        response.setData(data);

        return response;
    }

    // --- Методы валидации и проверки ---

    private void validateAccountForPriorityChange(Account account) {
        String status = account.getStatus();

        if (AccountStatus.CLOSED.toString().equalsIgnoreCase(status)) {
            throw new AccountValidationException(INVALID_ACCOUNT_STATE,
                    "Невозможно изменить приоритеты для закрытого счета");
        }

        if (AccountStatus.BLOCKED.toString().equalsIgnoreCase(status)) {
            Map<String, Object> details = new HashMap<>();
            details.put("accountId", account.getAccountId().toString());
            details.put("blockReason", "OVERDUE_PAYMENT");
            details.put("blockDate", ZonedDateTime.now().minusDays(7));

            throw new AccountValidationException(ACCOUNT_BLOCKED,
                    "Невозможно изменить приоритеты для заблокированного счета", details);
        }

        if (AccountStatus.FROZEN.toString().equalsIgnoreCase(status)) {
            throw new AccountValidationException(INVALID_ACCOUNT_STATE,
                    "Невозможно изменить приоритеты для замороженного счета");
        }
    }

    private void validatePriorityRules(Account account, AccountPriorityRequest request) {
        String accountType = account.getAccountType();
        Boolean writeOff = request.getPriorityForWriteOff();
        Boolean accrual = request.getPriorityForAccrual();

        // Проверки типа счета
        boolean isLoanAccount = accountType != null && accountType.startsWith("LOAN_");
        boolean isDepositAccount = accountType != null &&
                                   ("DEPOSIT".equalsIgnoreCase(accountType) || "сберегательный".equalsIgnoreCase(accountType));

        // Конфликт приоритетов для кредитных счетов
        if (isLoanAccount && Boolean.TRUE.equals(writeOff) && Boolean.TRUE.equals(accrual)) {
            throw new AccountValidationException(CONFLICT_PRIORITIES,
                    "Для кредитных счетов можно установить только один тип приоритета");
        }

        // Депозитные счета
        if (isDepositAccount) {
            throw new AccountValidationException(INVALID_ACCOUNT_STATE,
                    "Для депозитных счетов приоритеты не могут быть изменены");
        }

        // Проверки для приоритета списания
        if (Boolean.TRUE.equals(writeOff)) {
            // Отрицательный баланс
            if (account.getBalance() != null && account.getBalance().signum() < 0) {
                throw new AccountValidationException(INVALID_ACCOUNT_STATE,
                        "Невозможно установить приоритет списания для счета с отрицательным балансом");
            }
        }

        // Проверки для приоритета начисления
        if (Boolean.TRUE.equals(accrual)) {
            // Истекший срок действия
            boolean isExpired = account.getOpenedAt() != null &&
                                account.getOpenedAt().plusYears(5).isBefore(LocalDateTime.now());
            if (isExpired) {
                throw new AccountValidationException(INVALID_ACCOUNT_STATE,
                        "Невозможно установить приоритет начисления для счета с истекшим сроком действия");
            }
        }
    }

    private boolean canSetWriteOffPriority(Account account) {
        boolean isActiveAccount = AccountStatus.ACTIVE.toString().equalsIgnoreCase(account.getStatus());
        boolean hasPositiveBalance = !(account.getBalance() != null && account.getBalance().signum() < 0);

        return isActiveAccount && hasPositiveBalance;
    }

    private boolean canSetAccrualPriority(Account account) {
        String accountType = account.getAccountType();
        boolean isActiveAccount = AccountStatus.ACTIVE.toString().equalsIgnoreCase(account.getStatus());
        boolean isNotDepositAccount = !(accountType != null &&
                                        ("DEPOSIT".equalsIgnoreCase(accountType) ||
                                         "сберегательный".equalsIgnoreCase(accountType)));
        boolean hasNotExpiredTerm = !(account.getOpenedAt() != null &&
                                      account.getOpenedAt().plusYears(5).isBefore(LocalDateTime.now()));

        return isActiveAccount && isNotDepositAccount && hasNotExpiredTerm;
    }

    private List<String> getRestrictionReasons(Account account) {
        List<String> reasons = new ArrayList<>();
        String accountType = account.getAccountType();
        String status = account.getStatus();

        // Использование Map для сопоставления условий и сообщений
        Map<Boolean, String> reasonChecks = new HashMap<>();

        // Проверки типа счета
        reasonChecks.put(
                accountType != null && accountType.startsWith("LOAN_"),
                "Только один приоритет разрешен для кредитных счетов"
        );

        reasonChecks.put(
                accountType != null && ("DEPOSIT".equalsIgnoreCase(accountType) || "сберегательный".equalsIgnoreCase(accountType)),
                "Для депозитных счетов приоритеты не могут быть изменены"
        );

        // Проверки статуса счета
        reasonChecks.put(
                AccountStatus.CLOSED.toString().equalsIgnoreCase(status),
                "Невозможно изменить приоритеты для закрытого счета"
        );

        reasonChecks.put(
                AccountStatus.BLOCKED.toString().equalsIgnoreCase(status),
                "Невозможно изменить приоритеты для заблокированного счета"
        );

        reasonChecks.put(
                AccountStatus.FROZEN.toString().equalsIgnoreCase(status),
                "Невозможно изменить приоритеты для замороженного счета"
        );

        // Проверки баланса и срока действия
        reasonChecks.put(
                account.getBalance() != null && account.getBalance().signum() < 0,
                "Невозможно установить приоритет списания для счета с отрицательным балансом"
        );

        reasonChecks.put(
                account.getOpenedAt() != null && account.getOpenedAt().plusYears(5).isBefore(LocalDateTime.now()),
                "Невозможно установить приоритет начисления для счета с истекшим сроком действия"
        );

        // Добавление всех применимых причин
        reasonChecks.forEach((condition, message) -> {
            if (Boolean.TRUE.equals(condition)) {
                reasons.add(message);
            }
        });

        return reasons;
    }

    private void logPriorityChange(Account account, AccountPriorityData priorityData, AccountPriorityRequest request) {
        String initiatorInfo = request.getInitiator() != null ?
                request.getInitiator().getName() + " (" + request.getInitiator().getRole() + ")" : "N/A";

        log.info("Priority change for account {}: writeOff={}, accrual={}, reason={}, initiator={}",
                account.getAccountId(),
                priorityData.getPriorityForWriteOff(),
                priorityData.getPriorityForAccrual(),
                request.getReason(),
                initiatorInfo);
    }
}