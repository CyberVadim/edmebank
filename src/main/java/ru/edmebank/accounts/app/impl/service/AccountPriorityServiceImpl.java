package ru.edmebank.accounts.app.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.accounts.adapter.output.repository.AccountRepository;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.domain.entity.Account;
import ru.edmebank.accounts.domain.enums.AccountStatus;
import ru.edmebank.accounts.domain.enums.AccountType;
import ru.edmebank.accounts.fw.exception.AccountException;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.AccountPriorityGetData;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.AllowedChanges;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.CurrentPriorities;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.LastUpdated;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountPriorityServiceImpl implements AccountPriorityService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountPriorityResponse updatePriorities(String accountId, AccountPriorityUpdateRequest request)
            throws AccountException {
        log.debug("Обновление приоритетов для счета: {}", accountId);

        // Проверка и получение счета
        Account account = findAndValidateAccount(accountId);

        // Проверка возможности установки приоритетов
        validatePriorityUpdatePossibility(account, request);

        // Обновление приоритетов счета
        if (request.priorityForWriteOff() != null) {
            account.setPriorityForWriteOff(request.priorityForWriteOff());
        }

        if (request.priorityForAccrual() != null) {
            account.setPriorityForAccrual(request.priorityForAccrual());
        }

        // Установка информации об обновлении
        account.setLastUpdatedBy(request.updatedBy());
        account.setLastUpdatedDate(LocalDateTime.now());

        // Сохранение изменений
        Account savedAccount = accountRepository.save(account);

        // Формирование ответа
        return AccountPriorityResponse.success(new AccountPriorityResponse.AccountPriorityData(
                accountId,
                savedAccount.isPriorityForWriteOff(),
                savedAccount.isPriorityForAccrual(),
                LocalDateTime.now(),
                request.updatedBy()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountPriorityGetResponse getPriorities(String accountId) throws AccountException {
        log.debug("Получение приоритетов для счета: {}", accountId);

        // Проверка и получение счета
        Account account = findAndValidateAccount(accountId);

        // Определение разрешенных изменений
        AllowedChanges allowedChanges = determineAllowedChanges(account);

        // Формирование ответа
        return AccountPriorityGetResponse.success(new AccountPriorityGetData(
                accountId,
                account.getType(),
                new CurrentPriorities(
                        account.isPriorityForWriteOff(),
                        account.isPriorityForAccrual()
                ),
                allowedChanges,
                new LastUpdated(
                        account.getLastUpdatedDate(),
                        account.getLastUpdatedBy()
                )
        ));
    }

    private Account findAndValidateAccount(String accountId) throws AccountException {
        try {
            UUID id = UUID.fromString(accountId);
            return accountRepository.findById(id)
                    .orElseThrow(() -> new AccountException("ACCOUNT_NOT_FOUND", "Счет не найден", accountId));
        } catch (IllegalArgumentException e) {
            throw new AccountException("INVALID_REQUEST", "Неверный формат идентификатора счета", accountId);
        }
    }

    private void validatePriorityUpdatePossibility(Account account, AccountPriorityUpdateRequest request) throws AccountException {
        // Проверка статуса счета
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(
                    "INVALID_ACCOUNT_STATE",
                    "Невозможно установить приоритеты для неактивного счета",
                    Map.of("accountId", account.getId().toString(), "status", account.getStatus())
            );
        }

        // Проверка совместимости приоритетов (если применимо)
        if (Boolean.TRUE.equals(request.priorityForWriteOff()) && Boolean.TRUE.equals(request.priorityForAccrual())) {
            // Для некоторых типов счетов может быть запрещено устанавливать оба приоритета
            if (account.getType() == AccountType.DEPOSIT) {
                throw new AccountException(
                        "CONFLICT_PRIORITIES",
                        "Для депозитного счета нельзя одновременно установить оба приоритета",
                        Map.of("accountId", account.getId().toString())
                );
            }
        }
    }

    private AllowedChanges determineAllowedChanges(Account account) {
        boolean canSetWriteOff = true;
        boolean canSetAccrual = true;
        List<String> reasons = new ArrayList<>();

        // Проверки в зависимости от типа и статуса счета
        if (account.getStatus() != AccountStatus.ACTIVE) {
            canSetWriteOff = false;
            canSetAccrual = false;
            reasons.add("Счет не активен");
        }

        // Дополнительные проверки в зависимости от типа счета
        if (account.getType() == AccountType.CREDIT) {
            // Для кредитных счетов нельзя установить приоритет списания
            canSetWriteOff = false;
            reasons.add("Для кредитных счетов нельзя установить приоритет списания");
        } else if (account.getType() == AccountType.DEPOSIT) {
            // Для депозитных счетов нельзя установить приоритет начисления
            canSetAccrual = false;
            reasons.add("Для депозитных счетов нельзя установить приоритет начисления");
        }

        return new AllowedChanges(canSetWriteOff, canSetAccrual, reasons);
    }
}