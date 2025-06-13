package ru.edmebank.accounts.fw.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(String accountId) {
        super("Счет не найден", "ACCOUNT_NOT_FOUND", new AccountNotFoundDetails(accountId));
    }

    @Getter
    public static class AccountNotFoundDetails {
        private final String accountId;

        public AccountNotFoundDetails(String accountId) {
            this.accountId = accountId;
        }
    }
}