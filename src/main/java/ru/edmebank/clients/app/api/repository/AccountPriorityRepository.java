package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.contracts.enums.AccountPriorityStatus;

import java.util.UUID;

public interface AccountPriorityRepository extends JpaRepository<AccountPriority, UUID> {

    /**
     * Поиск активных настроек приоритетов по ID счета
     */
    AccountPriority findByAccount_AccountIdAndStatus(UUID accountId, AccountPriorityStatus status);
}