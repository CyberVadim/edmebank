package ru.edmebank.accounts.adapter.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.accounts.domain.entity.Account;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}