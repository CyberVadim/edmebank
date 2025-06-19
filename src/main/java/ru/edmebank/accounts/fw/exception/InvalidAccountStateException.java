package ru.edmebank.accounts.fw.exception;

import lombok.Getter;
import ru.edmebank.accounts.domain.enums.BlockReason;

import java.time.LocalDateTime;

@Getter
public class InvalidAccountStateException extends AccountException {

    public InvalidAccountStateException(String message, Object details) {
        super(message, "INVALID_ACCOUNT_STATE", details);
    }

    public static class AccountBlockedDetails {
        private final String accountId;
        private final BlockReason blockReason;
        private final LocalDateTime blockDate;

        public AccountBlockedDetails(String accountId, BlockReason blockReason, LocalDateTime blockDate) {
            this.accountId = accountId;
            this.blockReason = blockReason;
            this.blockDate = blockDate;
        }
    }
}