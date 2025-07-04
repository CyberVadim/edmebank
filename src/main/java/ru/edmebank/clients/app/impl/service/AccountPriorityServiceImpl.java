package ru.edmebank.clients.app.impl.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.clients.app.api.repository.AccountPriorityRepository;
import ru.edmebank.clients.app.api.repository.AccountRepository;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.clients.domain.entity.Account;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.clients.fw.exception.AccountPriorityException;
import ru.edmebank.clients.fw.security.JwtTokenUtil;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;
import ru.edmebank.contracts.enums.AccountPriorityStatus;
import ru.edmebank.contracts.enums.AccountPriorityType;
import ru.edmebank.contracts.enums.InitiatorRole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityServiceImpl implements AccountPriorityService {

    private static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    private static final String ACCOUNT_BLOCKED = "ACCOUNT_BLOCKED";
    private static final String INVALID_ACCOUNT_STATE = "INVALID_ACCOUNT_STATE";
    private static final String CONFLICT_PRIORITIES = "CONFLICT_PRIORITIES";
    private static final String RESTRICTION_NEGATIVE_BALANCE = "Счет с отрицательным балансом";
    private static final String RESTRICTION_ACCOUNT_CLOSED = "Счет закрыт";
    private static final String RESTRICTION_DEPOSIT_NO_CHANGES = "Приоритеты для депозитных счетов не изменяются";
    private static final String RESTRICTION_LOAN_ONE_PRIORITY = "Только один приоритет разрешен для кредитных счетов";
    private static final int SINGLE_RESTRICTION = 1;
    private static final int FIRST_RESTRICTION_INDEX = 0;
    private static final int INITIAL_VERSION = 1;
    private static final int VERSION_INCREMENT = 1;

    private final AccountRepository accountRepository;
    private final AccountPriorityRepository accountPriorityRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional(readOnly = true)
    public AccountPriorityDetailsResponse getAccountPriorityDetails(UUID accountId, String authHeader) throws AccountPriorityException {
        validateToken(authHeader);

        Account account = findAccountById(accountId);
        validateAccountIsActive(account);

        AccountPriority priority = accountPriorityRepository
                .findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);

        if (priority == null) {
            priority = createDefaultPriority(account);
        }

        List<String> restrictions = determineRestrictions(account);
        boolean canSetWriteOff = canSetWriteOff(restrictions);
        boolean canSetAccrual = canSetAccrual(restrictions);

        return buildPriorityDetailsResponse(account, priority, canSetWriteOff, canSetAccrual, restrictions);
    }

    @Override
    @Transactional
    public AccountPriorityUpdateResponse updateAccountPriority(
            UUID accountId, AccountPriorityUpdateRequest request, String authHeader) throws AccountPriorityException {

        String token = validateToken(authHeader);
        validateInitiator(token, request.getInitiator().getId());

        Account account = findAccountById(accountId);
        validateAccountIsActive(account);
        validateBusinessRules(account, request);

        // Архивируем существующую запись, если есть
        AccountPriority existingPriority = accountPriorityRepository
                .findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);

        if (existingPriority != null) {
            existingPriority.setStatus(AccountPriorityStatus.ARCHIVED);
        }

        // Создаем и сохраняем новую запись
        int version = (existingPriority != null) ? existingPriority.getVersion() + VERSION_INCREMENT : INITIAL_VERSION;
        AccountPriority newPriority = createNewPriority(account, request, version);

        return buildUpdateResponse(account, newPriority);
    }

    private String validateToken(String authHeader) {
        String token = jwtTokenUtil.extractTokenFromHeader(authHeader);

        if (token == null) {
            throw new AccountPriorityException(
                    HttpStatus.UNAUTHORIZED.name(),
                    "Отсутствует JWT токен в заголовке Authorization",
                    HttpStatus.UNAUTHORIZED);
        }

        if (!jwtTokenUtil.validateToken(token)) {
            throw new AccountPriorityException(
                    HttpStatus.UNAUTHORIZED.name(),
                    "Невалидный JWT токен",
                    HttpStatus.UNAUTHORIZED);
        }
        return token;
    }

    private void validateInitiator(String token, String initiatorId) {
        Claims claims = jwtTokenUtil.getClaims(token);

        if (claims == null) {
            throw new AccountPriorityException(
                    HttpStatus.UNAUTHORIZED.name(),
                    "Невозможно извлечь данные из JWT токена",
                    HttpStatus.UNAUTHORIZED);
        }

        String tokenUserId = claims.getSubject();

        if (!tokenUserId.equals(initiatorId)) {
            throw new AccountPriorityException(
                    HttpStatus.FORBIDDEN.name(),
                    "ID пользователя в токене не совпадает с ID инициатора в запросе",
                    HttpStatus.FORBIDDEN);
        }
    }

    private Account findAccountById(UUID accountId) throws AccountPriorityException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountPriorityException(
                        ACCOUNT_NOT_FOUND,
                        "Счет не найден",
                        HttpStatus.NOT_FOUND));
    }


    private void validateAccountIsActive(Account account) throws AccountPriorityException {
        switch (account.getStatus()) {
            case "открыт": return;
            case "заблокирован":
                throw new AccountPriorityException(
                        ACCOUNT_BLOCKED,
                        "Невозможно изменить приоритеты для заблокированного счета",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            default:
                throw new AccountPriorityException(
                        INVALID_ACCOUNT_STATE,
                        "Недопустимое состояние счета: " + account.getStatus(),
                        HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Проверка бизнес-правил при обновлении приоритетов
     */
    private void validateBusinessRules(Account account, AccountPriorityUpdateRequest request) throws AccountPriorityException {
        List<String> restrictions = determineRestrictions(account);

        // Проверка возможности установки приоритетов
        if (request.getPriorityForWriteOff() && !canSetWriteOff(restrictions)) {
            throw new AccountPriorityException(
                    INVALID_ACCOUNT_STATE,
                    "Невозможно установить приоритет для списания: " + String.join(", ", restrictions),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (request.getPriorityForAccrual() && !canSetAccrual(restrictions)) {
            throw new AccountPriorityException(
                    INVALID_ACCOUNT_STATE,
                    "Невозможно установить приоритет для начисления: " + String.join(", ", restrictions),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // Для кредитных счетов можно установить только один тип приоритета
        AccountPriorityType accountType = determineAccountType(account);
        boolean isLoanAccount = accountType == AccountPriorityType.LOAN_PRINCIPAL ||
                                accountType == AccountPriorityType.LOAN_INTEREST;

        if (isLoanAccount && request.getPriorityForWriteOff() && request.getPriorityForAccrual()) {
            throw new AccountPriorityException(
                    CONFLICT_PRIORITIES,
                    "Для кредитных счетов можно установить только один тип приоритета",
                    HttpStatus.CONFLICT);
        }
    }

    private AccountPriority createDefaultPriority(Account account) {
        AccountPriority priority = new AccountPriority();
        priority.setAccount(account);
        priority.setAccountType(determineAccountType(account).name());
        priority.setPriorityWriteOff(false);
        priority.setPriorityAccrual(false);
        priority.setCanSetWriteOff(true);
        priority.setCanSetAccrual(true);
        priority.setChangeReasons(Collections.emptyList());
        priority.setChangeReason("Инициализация приоритетов");
        priority.setInitiatorId(InitiatorRole.SYSTEM.name());
        priority.setInitiatorName("Система");
        priority.setInitiatorRole(InitiatorRole.SYSTEM.name());
        priority.setLastUpdatedBy("Система (SYSTEM)");
        priority.setStatus(AccountPriorityStatus.ACTIVE);
        return priority;
    }

    /**
     * Создание нового объекта приоритетов
     */
    private AccountPriority createNewPriority(Account account, AccountPriorityUpdateRequest request, int version) {
        List<String> restrictions = determineRestrictions(account);
        AccountPriorityType accountType = determineAccountType(account);

        AccountPriority priority = new AccountPriority();
        priority.setAccount(account);
        priority.setAccountType(accountType.name());
        priority.setPriorityWriteOff(request.getPriorityForWriteOff());
        priority.setPriorityAccrual(request.getPriorityForAccrual());
        priority.setCanSetWriteOff(canSetWriteOff(restrictions));
        priority.setCanSetAccrual(canSetAccrual(restrictions));
        priority.setChangeReasons(restrictions.isEmpty() ? Collections.emptyList() : restrictions);
        priority.setChangeReason(request.getReason());
        priority.setInitiatorId(request.getInitiator().getId());
        priority.setInitiatorName(request.getInitiator().getName());
        priority.setInitiatorRole(request.getInitiator().getRole().name());
        priority.setLastUpdatedBy(request.getInitiator().getName() + " (" + request.getInitiator().getRole() + ")");
        priority.setVersion(version);
        priority.setStatus(AccountPriorityStatus.ACTIVE);
        return priority;
    }

    /**
     * Определение ограничений для счета
     */
    private List<String> determineRestrictions(Account account) {
        List<String> restrictions = new ArrayList<>();
        AccountPriorityType accountType = determineAccountType(account);

        // Проверки для ограничений по приоритету списания
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            restrictions.add(RESTRICTION_NEGATIVE_BALANCE);
        }

        // Проверки для ограничений по приоритету начисления
        if (account.getClosedAt() != null) {
            restrictions.add(RESTRICTION_ACCOUNT_CLOSED);
        }

        // Дополнительные проверки в зависимости от типа счета
        switch (accountType) {
            case DEPOSIT:
                restrictions.add(RESTRICTION_DEPOSIT_NO_CHANGES);
                break;
            case LOAN_PRINCIPAL:
            case LOAN_INTEREST:
                restrictions.add(RESTRICTION_LOAN_ONE_PRIORITY);
                break;
        }

        return restrictions;
    }

    /**
     * Проверка возможности установки приоритета для списания
     */
    private boolean canSetWriteOff(List<String> restrictions) {
        return restrictions.isEmpty() ||
               (restrictions.size() == SINGLE_RESTRICTION &&
                RESTRICTION_LOAN_ONE_PRIORITY.equals(restrictions.get(FIRST_RESTRICTION_INDEX)));
    }

    /**
     * Проверка возможности установки приоритета для начисления
     */
    private boolean canSetAccrual(List<String> restrictions) {
        return restrictions.isEmpty() ||
               (restrictions.size() == SINGLE_RESTRICTION &&
                RESTRICTION_LOAN_ONE_PRIORITY.equals(restrictions.get(FIRST_RESTRICTION_INDEX)));
    }

    /**
     * Определение типа счета
     */
    private AccountPriorityType determineAccountType(Account account) {
        String accountType = account.getAccountType();

        if (accountType == null) {
            return AccountPriorityType.DEPOSIT;
        }

        String lowerType = accountType.toLowerCase();

        // Определение типа счета по подстрокам в названии
        boolean isLoanAccount =
                lowerType.contains(AccountPriorityType.LOAN_PRINCIPAL.name()) || lowerType.contains("кредит");
        boolean isInterestAccount =
                lowerType.contains(AccountPriorityType.LOAN_INTEREST.name()) || lowerType.contains("процент");
        boolean isDepositAccount =
                lowerType.contains(AccountPriorityType.DEPOSIT.name()) || lowerType.contains("депозит");

        if (isLoanAccount) {
            if (isInterestAccount) {
                return AccountPriorityType.LOAN_INTEREST;
            }
            return AccountPriorityType.LOAN_PRINCIPAL;
        }

        if (isDepositAccount) {
            return AccountPriorityType.DEPOSIT;
        }

        return AccountPriorityType.DEPOSIT; // По умолчанию
    }

    /**
     * Построение ответа с деталями приоритетов
     */
    private AccountPriorityDetailsResponse buildPriorityDetailsResponse(
            Account account, AccountPriority priority, boolean canSetWriteOff, boolean canSetAccrual, List<String> restrictions) {

        // Создаем вложенные объекты
        AccountPriorityDetailsResponse.CurrentPriorities currentPriorities =
                AccountPriorityDetailsResponse.CurrentPriorities.builder()
                        .priorityForWriteOff(priority.getPriorityWriteOff())
                        .priorityForAccrual(priority.getPriorityAccrual())
                        .build();

        AccountPriorityDetailsResponse.AllowedChanges allowedChanges =
                AccountPriorityDetailsResponse.AllowedChanges.builder()
                        .canSetWriteOff(canSetWriteOff)
                        .canSetAccrual(canSetAccrual)
                        .reasons(restrictions)
                        .build();

        AccountPriorityDetailsResponse.LastUpdated lastUpdated =
                AccountPriorityDetailsResponse.LastUpdated.builder()
                        .date(priority.getUpdatedAt())
                        .by(priority.getLastUpdatedBy())
                        .build();

        // Создаем основной объект ответа
        return AccountPriorityDetailsResponse.builder()
                .accountId(account.getAccountId())
                .accountType(AccountPriorityType.valueOf(priority.getAccountType()))
                .currentPriorities(currentPriorities)
                .allowedChanges(allowedChanges)
                .lastUpdated(lastUpdated)
                .build();
    }

    /**
     * Построение ответа об обновлении приоритетов
     */
    private AccountPriorityUpdateResponse buildUpdateResponse(Account account, AccountPriority priority) {
        // Создаем вложенный объект
        AccountPriorityUpdateResponse.UpdatedPriorities updatedPriorities =
                AccountPriorityUpdateResponse.UpdatedPriorities.builder()
                        .priorityForWriteOff(priority.getPriorityWriteOff())
                        .priorityForAccrual(priority.getPriorityAccrual())
                        .build();

        // Создаем основной объект ответа
        return AccountPriorityUpdateResponse.builder()
                .accountId(account.getAccountId())
                .updatedPriorities(updatedPriorities)
                .updatedAt(priority.getUpdatedAt())
                .version(priority.getVersion())
                .build();
    }
}