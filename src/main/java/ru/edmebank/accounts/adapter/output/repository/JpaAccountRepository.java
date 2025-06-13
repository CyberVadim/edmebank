package ru.edmebank.accounts.adapter.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.edmebank.accounts.domain.entity.Account;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, String> {
}