package ru.edmebank.accounts.app.api.repository;

import java.util.Optional;
import ru.edmebank.accounts.domain.entity.Account;

public interface AccountRepository {

    Optional<Account> findById(String accountId);

    Account save(Account account);
}