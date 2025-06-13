package ru.edmebank.accounts.adapter.output.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.edmebank.accounts.app.api.repository.AccountRepository;
import ru.edmebank.accounts.domain.entity.Account;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Optional<Account> findById(String accountId) {
        return jpaAccountRepository.findById(accountId);
    }

    @Override
    public Account save(Account account) {
        return jpaAccountRepository.save(account);
    }
}