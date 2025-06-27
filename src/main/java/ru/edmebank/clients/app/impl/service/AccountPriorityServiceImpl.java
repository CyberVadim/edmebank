package ru.edmebank.clients.app.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.clients.app.api.exception.ApiException;
import ru.edmebank.clients.app.api.exception.ErrorCode;
import ru.edmebank.clients.app.api.repository.AccountPriorityRepository;
import ru.edmebank.clients.app.api.repository.AccountRepository;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.clients.domain.entity.Account;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;
import ru.edmebank.contracts.enums.AccountPriorityStatus;
import ru.edmebank.contracts.enums.AccountPriorityType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с приоритетами счетов
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityServiceImpl implements AccountPriorityService {

    private final AccountRepository accountRepository;
    private final AccountPriorityRepository accountPriorityRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityDetailsResponse getAccountPriorityDetails(UUID accountId) throws ApiException {
        Account account = findAccountById(accountId);
        validateAccountIsActive(account);

        AccountPriority priority = accountPriorityRepository
                .findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE)
                .orElse(createDefaultPriority(account));

        // Определение возможных ограничений на изменение приоритетов
        List<String> restrictions = determineRestrictions(account);
        boolean canSetWriteOff = canSetWriteOff(account, restrictions);
        boolean canSetAccrual = canSetAccrual(account, restrictions);

        return buildPriorityDetailsResponse(account, priority, canSetWriteOff, canSetAccrual, restrictions);
    }

    @Override
    @Transactional
    public AccountPriorityUpdateResponse updateAccountPriority(
            UUID accountId, AccountPriorityUpdateRequest request) throws ApiException {

        Account account = findAccountById(accountId);
        validateAccountIsActive(account);

        // Проверка бизнес-правил
        validateBusinessRules(account, request);

        // Получение или создание записи о приоритетах
        AccountPriority existingPriority = accountPriorityRepository
                .findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE)
                .orElse(null);

        // Если есть существующая запись, архивируем её
        if (existingPriority != null) {
            existingPriority.setStatus(AccountPriorityStatus.ARCHIVED);
            accountPriorityRepository.save(existingPriority);
        }

        // Создаем новую запись с обновленными приоритетами
        AccountPriority newPriority = createNewPriority(account, request,
                existingPriority != null ? existingPriority.getVersion() + 1 : 1);
        accountPriorityRepository.save(newPriority);

        // Логирование изменений
        log.info("Updated account priorities for accountId: {}. WriteOff: {}, Accrual: {}, Initiator: {} ({})",
                accountId, request.getPriorityForWriteOff(), request.getPriorityForAccrual(),
                request.getInitiator().getName(), request.getInitiator().getRole());

        return buildUpdateResponse(account, newPriority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountPriorityDetailsResponse> getAccountPriorityHistory(UUID accountId) throws ApiException {
        Account account = findAccountById(accountId);

        List<AccountPriority> historyRecords = accountPriorityRepository.findAccountPriorityHistory(accountId);

        return historyRecords.stream()
                .map(priority -> {
                    List<String> restrictions = Collections.emptyList();
                    if (priority.getChangeReasons() != null && !priority.getChangeReasons().isEmpty()) {
                        restrictions = Arrays.asList(priority.getChangeReasons().split(","));
                    }

                    return buildPriorityDetailsResponse(
                            account,
                            priority,
                            priority.getCanSetWriteOff(),
                            priority.getCanSetAccrual(),
                            restrictions
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSetWriteOffPriority(UUID accountId) throws ApiException {
        Account account = findAccountById(accountId);
        List<String> restrictions = determineRestrictions(account);
        return canSetWriteOff(account, restrictions);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSetAccrualPriority(UUID accountId) throws ApiException {
        Account account = findAccountById(accountId);
        List<String> restrictions = determineRestrictions(account);
        return canSetAccrual(account, restrictions);
    }

    // Вспомогательные методы

    private Account findAccountById(UUID accountId) throws ApiException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND,
                        "Счет не найден", Map.of("accountId", accountId.toString())));
    }

    private void validateAccountIsActive(Account account) throws ApiException {
        if (!"открыт".equals(account.getStatus())) {
            throw new ApiException(ErrorCode.INVALID_ACCOUNT_STATE,
                    "Недопустимое состояние счета",
                    Map.of(
                            "accountId", account.getAccountId().toString(),
                            "status", account.getStatus()
                    ));
        }
    }

    private void validateBusinessRules(Account account, AccountPriorityUpdateRequest request) throws ApiException {
        List<String> restrictions = determineRestrictions(account);

        // Проверка возможности установки приоритетов
        if (request.getPriorityForWriteOff() && !canSetWriteOff(account, restrictions)) {
            throw new ApiException(ErrorCode.INVALID_ACCOUNT_STATE,
                    "Невозможно установить приоритет для списания",
                    Map.of(
                            "accountId", account.getAccountId().toString(),
                            "reasons", restrictions
                    ));
        }

        if (request.getPriorityForAccrual() && !canSetAccrual(account, restrictions)) {
            throw new ApiException(ErrorCode.INVALID_ACCOUNT_STATE,
                    "Невозможно установить приоритет для начисления",
                    Map.of(
                            "accountId", account.getAccountId().toString(),
                            "reasons", restrictions
                    ));
        }

        // Для кредитных счетов можно установить только один тип приоритета
        AccountPriorityType accountType = determineAccountType(account);
        if ((accountType == AccountPriorityType.LOAN_PRINCIPAL || accountType == AccountPriorityType.LOAN_INTEREST)
            && request.getPriorityForWriteOff() && request.getPriorityForAccrual()) {
            throw new ApiException(ErrorCode.CONFLICT_PRIORITIES,
                    "Для кредитных счетов можно установить только один тип приоритета",
                    Map.of("accountId", account.getAccountId().toString()));
        }
    }

    private AccountPriority createDefaultPriority(Account account) {
        AccountPriority priority = new AccountPriority();
        priority.setAccount(account);
        priority.setAccountType(determineAccountType(account));
        priority.setPriorityWriteOff(false);
        priority.setPriorityAccrual(false);
        priority.setCanSetWriteOff(true);
        priority.setCanSetAccrual(true);
        priority.setChangeReason("Инициализация приоритетов");
        priority.setInitiatorId("SYSTEM");
        priority.setInitiatorName("Система");
        priority.setInitiatorRole(ru.edmebank.clients.domain.enums.InitiatorRole.SYSTEM);
        priority.setLastUpdatedBy("Система (SYSTEM)");
        priority.setStatus(AccountPriorityStatus.ACTIVE);
        return priority;
    }

    private AccountPriority createNewPriority(Account account, AccountPriorityUpdateRequest request, int version) {
        AccountPriority priority = new AccountPriority();
        priority.setAccount(account);
        priority.setAccountType(determineAccountType(account));
        priority.setPriorityWriteOff(request.getPriorityForWriteOff());
        priority.setPriorityAccrual(request.getPriorityForAccrual());

        // Определение ограничений
        List<String> restrictions = determineRestrictions(account);
        priority.setCanSetWriteOff(canSetWriteOff(account, restrictions));
        priority.setCanSetAccrual(canSetAccrual(account, restrictions));
        priority.setChangeReasons(String.join(",", restrictions));

        // Информация об инициаторе
        priority.setChangeReason(request.getReason());
        priority.setInitiatorId(request.getInitiator().getId());
        priority.setInitiatorName(request.getInitiator().getName());
        priority.setInitiatorRole(request.getInitiator().getRole());
        priority.setLastUpdatedBy(request.getInitiator().getName() + " (" + request.getInitiator().getRole() + ")");
        priority.setVersion(version);
        priority.setStatus(AccountPriorityStatus.ACTIVE);

        return priority;
    }

    private List<String> determineRestrictions(Account account) {
        List<String> restrictions = new ArrayList<>();

        // Проверки для ограничений по приоритету списания
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            restrictions.add("Счет с отрицательным балансом");
        }

        // Проверки для ограничений по приоритету начисления
        if (account.getClosedAt() != null) {
            restrictions.add("Счет закрыт");
        }

        // Дополнительные проверки в зависимости от типа счета
        AccountPriorityType accountType = determineAccountType(account);
        if (accountType == AccountPriorityType.DEPOSIT) {
            restrictions.add("Приоритеты для депозитных счетов не изменяются");
        } else if (accountType == AccountPriorityType.LOAN_PRINCIPAL || accountType == AccountPriorityType.LOAN_INTEREST) {
            restrictions.add("Только один приоритет разрешен для кредитных счетов");
        }

        return restrictions;
    }

    private boolean canSetWriteOff(Account account, List<String> restrictions) {
        // Счет можно сделать приоритетным для списания, если нет ограничений
        // или есть только ограничение на одновременную установку двух приоритетов
        return restrictions.isEmpty() ||
               (restrictions.size() == 1 &&
                restrictions.get(0).equals("Только один приоритет разрешен для кредитных счетов"));
    }

    private boolean canSetAccrual(Account account, List<String> restrictions) {
        // Счет можно сделать приоритетным для начисления, если нет ограничений
        // или есть только ограничение на одновременную установку двух приоритетов
        return restrictions.isEmpty() ||
               (restrictions.size() == 1 &&
                restrictions.get(0).equals("Только один приоритет разрешен для кредитных счетов"));
    }

    private AccountPriorityType determineAccountType(Account account) {
        // Определение типа счета на основе информации из account
        // В реальном проекте это может быть основано на дополнительных атрибутах или связанных записях
        String accountType = account.getAccountType();
        if (accountType == null) {
            return AccountPriorityType.DEPOSIT; // По умолчанию
        }

        if (accountType.toLowerCase().contains("кредит") || accountType.toLowerCase().contains("loan")) {
            if (accountType.toLowerCase().contains("interest") || accountType.toLowerCase().contains("процент")) {
                return AccountPriorityType.LOAN_INTEREST;
            }
            return AccountPriorityType.LOAN_PRINCIPAL;
        }

        if (accountType.toLowerCase().contains("депозит") || accountType.toLowerCase().contains("deposit")) {
            return AccountPriorityType.DEPOSIT;
        }

        return AccountPriorityType.DEPOSIT; // По умолчанию
    }

    private AccountPriorityDetailsResponse buildPriorityDetailsResponse(
            Account account, AccountPriority priority, boolean canSetWriteOff, boolean canSetAccrual, List<String> restrictions) {

        return AccountPriorityDetailsResponse.builder()
                .accountId(account.getAccountId())
                .accountType(priority.getAccountType())
                .currentPriorities(AccountPriorityDetailsResponse.CurrentPriorities.builder()
                        .priorityForWriteOff(priority.getPriorityWriteOff())
                        .priorityForAccrual(priority.getPriorityAccrual())
                        .build())
                .allowedChanges(AccountPriorityDetailsResponse.AllowedChanges.builder()
                        .canSetWriteOff(canSetWriteOff)
                        .canSetAccrual(canSetAccrual)
                        .reasons(restrictions)
                        .build())
                .lastUpdated(AccountPriorityDetailsResponse.LastUpdated.builder()
                        .date(priority.getUpdatedAt())
                        .by(priority.getLastUpdatedBy())
                        .build())
                .build();
    }

    private AccountPriorityUpdateResponse buildUpdateResponse(Account account, AccountPriority priority) {
        return AccountPriorityUpdateResponse.builder()
                .accountId(account.getAccountId())
                .updatedPriorities(AccountPriorityUpdateResponse.UpdatedPriorities.builder()
                        .priorityForWriteOff(priority.getPriorityWriteOff())
                        .priorityForAccrual(priority.getPriorityAccrual())
                        .build())
                .updatedAt(priority.getUpdatedAt())
                .version(priority.getVersion())
                .build();
    }
}