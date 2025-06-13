package ru.edmebank.accounts.app.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityGetResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityUpdateRequest;
import ru.edmebank.accounts.app.api.repository.AccountRepository;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.domain.entity.Account;
import ru.edmebank.accounts.domain.entity.AccountStatus;
import ru.edmebank.accounts.domain.entity.AccountType;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.ConflictPrioritiesException;
import ru.edmebank.accounts.fw.exception.InvalidAccountStateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityServiceImpl implements AccountPriorityService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountPriorityResponse updatePriorities(String accountId, AccountPriorityUpdateRequest request) {
        log.info("Обновление приоритетов для счета: {}", accountId);

        // Получаем счет из репозитория
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Проверка состояния счета
        validateAccountState(account);

        // Проверка конфликтов приоритетов для кредитных счетов
        validatePriorityConflicts(account.getAccountType(), request.getPriorityForWriteOff(),
                request.getPriorityForAccrual());

        // Обновляем приоритеты
        account.setPriorityForWriteOff(request.getPriorityForWriteOff());
        account.setPriorityForAccrual(request.getPriorityForAccrual());
        account.setLastUpdatedBy(formatInitiatorInfo(request));

        // Сохраняем изменения
        Account savedAccount = accountRepository.save(account);

        // Логируем изменение (в реальной системе здесь должна быть отправка в аудит-лог)
        log.info("Приоритеты счета {} обновлены. Причина: {}", accountId, request.getReason());

        // Возвращаем успешный ответ
        return AccountPriorityResponse.success(
                savedAccount.getId(),
                savedAccount.isPriorityForWriteOff(),
                savedAccount.isPriorityForAccrual(),
                savedAccount.getUpdatedAt(),
                savedAccount.getVersion()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityGetResponse getPriorities(String accountId) {
        log.info("Получение приоритетов для счета: {}", accountId);

        // Получаем счет из репозитория
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Определяем доступные изменения
        boolean canSetWriteOff = canSetWriteOffPriority(account);
        boolean canSetAccrual = canSetAccrualPriority(account);
        List<String> reasons = getRestrictionReasons(account, canSetWriteOff, canSetAccrual);

        // Строим ответ
        AccountPriorityGetResponse.AccountPriorityGetData data =
                AccountPriorityGetResponse.AccountPriorityGetData.builder()
                        .accountId(account.getId())
                        .accountType(account.getAccountType())
                        .currentPriorities(
                                AccountPriorityGetResponse.CurrentPriorities.builder()
                                        .priorityForWriteOff(account.isPriorityForWriteOff())
                                        .priorityForAccrual(account.isPriorityForAccrual())
                                        .build()
                        )
                        .allowedChanges(
                                AccountPriorityGetResponse.AllowedChanges.builder()
                                        .canSetWriteOff(canSetWriteOff)
                                        .canSetAccrual(canSetAccrual)
                                        .reasons(reasons)
                                        .build()
                        )
                        .lastUpdated(
                                AccountPriorityGetResponse.LastUpdated.builder()
                                        .date(account.getUpdatedAt())
                                        .by(account.getLastUpdatedBy())
                                        .build()
                        )
                        .build();

        return AccountPriorityGetResponse.success(data);
    }

    /**
     * Проверяет состояние счета перед изменением приоритетов
     */
    private void validateAccountState(Account account) {
        // Проверка активности счета
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                    "Невозможно изменить приоритеты для счета в статусе " + account.getStatus(),
                    null
            );
        }

        // Проверка блокировки счета
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountStateException(
                    "Невозможно изменить приоритеты для заблокированного счета",
                    new InvalidAccountStateException.AccountBlockedDetails(
                            account.getId(),
                            account.getBlockReason(),
                            account.getBlockDate()
                    )
            );
        }
    }

    /**
     * Проверяет конфликты приоритетов в зависимости от типа счета
     */
    private void validatePriorityConflicts(AccountType accountType, Boolean priorityForWriteOff,
                                           Boolean priorityForAccrual) {
        // Для кредитных счетов можно установить только один тип приоритета
        if ((accountType == AccountType.LOAN_PRINCIPAL || accountType == AccountType.LOAN_INTEREST)
            && priorityForWriteOff && priorityForAccrual) {
            throw new ConflictPrioritiesException();
        }

        // Для депозитных счетов приоритеты не изменяются
        if (accountType == AccountType.DEPOSIT) {
            throw new InvalidAccountStateException(
                    "Невозможно изменить приоритеты для депозитного счета",
                    null
            );
        }
    }

    /**
     * Форматирует информацию об инициаторе изменения
     */
    private String formatInitiatorInfo(AccountPriorityUpdateRequest request) {
        return String.format("%s (%s)",
                request.getInitiator().getName(),
                request.getInitiator().getRole());
    }

    /**
     * Проверяет, можно ли установить приоритет для списания
     */
    private boolean canSetWriteOffPriority(Account account) {
        // Счет должен быть активным
        if (account.getStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        // Счет не должен быть заблокирован
        if (account.getStatus() == AccountStatus.BLOCKED) {
            return false;
        }

        // Для депозитных счетов приоритеты не изменяются
        if (account.getAccountType() == AccountType.DEPOSIT) {
            return false;
        }

        // Нельзя установить приоритет списания, если уже установлен приоритет начисления
        // для кредитных счетов
        if ((account.getAccountType() == AccountType.LOAN_PRINCIPAL
             || account.getAccountType() == AccountType.LOAN_INTEREST)
            && account.isPriorityForAccrual()) {
            return false;
        }

        return true;
    }

    /**
     * Проверяет, можно ли установить приоритет для начисления
     */
    private boolean canSetAccrualPriority(Account account) {

        // Счет должен быть активным
        if (account.getStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        // Счет не должен быть заблокирован
        if (account.getStatus() == AccountStatus.BLOCKED) {
            return false;
        }

        // Для депозитных счетов приоритеты не изменяются
        if (account.getAccountType() == AccountType.DEPOSIT) {
            return false;
        }

        // Проверка срока действия
        if (account.getExpirationDate() != null && account.getExpirationDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Нельзя установить приоритет начисления, если уже установлен приоритет списания
        // для кредитных счетов
        if ((account.getAccountType() == AccountType.LOAN_PRINCIPAL
             || account.getAccountType() == AccountType.LOAN_INTEREST)
            && account.isPriorityForWriteOff()) {
            return false;
        }

        return true;
    }

    /**
     * Возвращает причины ограничений для установки приоритетов
     */
    private List<String> getRestrictionReasons(Account account, boolean canSetWriteOff, boolean canSetAccrual) {
        List<String> reasons = new ArrayList<>();

        if (account.getAccountType() == AccountType.DEPOSIT) {
            reasons.add("Для депозитных счетов приоритеты не изменяются");
            return reasons;
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            reasons.add("Счет не активен");
            return reasons;
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            reasons.add("Счет заблокирован");
            return reasons;
        }

        if ((account.getAccountType() == AccountType.LOAN_PRINCIPAL
             || account.getAccountType() == AccountType.LOAN_INTEREST)) {
            reasons.add("Только один приоритет разрешен для кредитных счетов");
        }

        if (!canSetAccrual && account.getExpirationDate() != null
            && account.getExpirationDate().isBefore(LocalDateTime.now())) {
            reasons.add("Нельзя установить приоритет начисления для счета с истекшим сроком действия");
        }

        return reasons;
    }
}