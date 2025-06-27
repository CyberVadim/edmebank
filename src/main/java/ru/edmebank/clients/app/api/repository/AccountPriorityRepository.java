package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.edmebank.clients.domain.entity.Account;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.contracts.enums.AccountPriorityStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountPriorityRepository extends JpaRepository<AccountPriority, UUID> {

    // Найти активные приоритеты для счета
    Optional<AccountPriority> findByAccountAndStatus(Account account, AccountPriorityStatus status);

    // Найти все приоритеты для счета (включая архивные)
    List<AccountPriority> findAllByAccount(Account account);

    // Найти активный приоритет по account_id
    Optional<AccountPriority> findByAccount_AccountIdAndStatus(UUID accountId, AccountPriorityStatus status);

    // Получить историю изменений приоритетов для счета
    @Query("SELECT ap FROM AccountPriority ap WHERE ap.account.accountId = :accountId ORDER BY ap.updatedAt DESC")
    List<AccountPriority> findAccountPriorityHistory(@Param("accountId") UUID accountId);
}