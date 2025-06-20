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
        log.debug("Обновление приоритетов для счета: {}. Инициатор: {}", accountId, request.getInitiator().getName());

        // Проверка и получение счета
        Account account = findAndValidateAccount(accountId);

        // Проверка возможности установки приоритетов
        validatePriorityUpdatePossibility(account, request);

        // Обновление приоритетов счета
        account.setPriorityForWriteOff(request.getPriorityForWriteOff());
        account.setPriorityForAccrual(request.getPriorityForAccrual());

        // Формируем информацию об инициаторе изменений
        String initiatorInfo = String.format("%s (%s, %s)",
                request.getInitiator().getName(),
                request.getInitiator().getId(),
                request.getInitiator().getRole().name());

        // Установка информации об обновлении
        account.setLastUpdatedBy(initiatorInfo);
        // updatedAt обновится автоматически через @UpdateTimestamp

        // Сохранение изменений
        Account savedAccount = accountRepository.save(account);

        // Формирование ответа
        return AccountPriorityResponse.success(
                accountId,
                savedAccount.isPriorityForWriteOff(),
                savedAccount.isPriorityForAccrual(),
                savedAccount.getUpdatedAt(),
                savedAccount.getVersion()
        );
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
                account.getAccountType(),
                new CurrentPriorities(
                        account.isPriorityForWriteOff(),
                        account.isPriorityForAccrual()
                ),
                allowedChanges,
                new LastUpdated(
                        account.getUpdatedAt(),
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

        // Проверка совместимости приоритетов
        if (Boolean.TRUE.equals(request.getPriorityForWriteOff()) && Boolean.TRUE.equals(request.getPriorityForAccrual())) {
            // Для некоторых типов счетов может быть запрещено устанавливать оба приоритета
            if (account.getAccountType() == AccountType.DEPOSIT) {
                throw new AccountException(
                        "CONFLICT_PRIORITIES",
                        "Для депозитного счета нельзя одновременно установить оба приоритета",
                        Map.of("accountId", account.getId().toString())
                );
            }
        }

        // Проверка прав доступа инициатора
        if (request.getInitiator().getRole() == AccountPriorityUpdateRequest.InitiatorRole.CLIENT) {
            // Например, клиент может не иметь права менять приоритет списания для кредитных счетов
            if ((account.getAccountType() == AccountType.LOAN_PRINCIPAL ||
                 account.getAccountType() == AccountType.LOAN_INTEREST) &&
                request.getPriorityForWriteOff() != null) {
                throw new AccountException(
                        "FORBIDDEN",
                        "Клиент не имеет права менять приоритет списания для кредитных счетов",
                        Map.of("accountId", account.getId().toString(), "initiatorRole", request.getInitiator().getRole())
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
        if (account.getAccountType() == AccountType.LOAN_PRINCIPAL ||
            account.getAccountType() == AccountType.LOAN_INTEREST) {
            // Для кредитных счетов нельзя установить приоритет списания
            canSetWriteOff = false;
            reasons.add("Для кредитных счетов нельзя установить приоритет списания");
        } else if (account.getAccountType() == AccountType.DEPOSIT) {
            // Для депозитных счетов нельзя установить приоритет начисления
            canSetAccrual = false;
            reasons.add("Для депозитных счетов нельзя установить приоритет начисления");
        }

        return new AllowedChanges(canSetWriteOff, canSetAccrual, reasons);
    }
}