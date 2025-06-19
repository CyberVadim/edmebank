package ru.edmebank.contracts.dto.accounts;

import ru.edmebank.accounts.domain.enums.AccountType;

import java.time.LocalDateTime;
import java.util.List;

public record AccountPriorityGetResponse(
        String status,
        AccountPriorityGetData data,
        ErrorData error
) {
    public AccountPriorityGetResponse(AccountPriorityGetData data, ErrorData error) {
        this("SUCCESS", data, error);
    }

    public record AccountPriorityGetData(
            String accountId,
            AccountType accountType,
            CurrentPriorities currentPriorities,
            AllowedChanges allowedChanges,
            LastUpdated lastUpdated
    ) {}

    public record CurrentPriorities(
            Boolean priorityForWriteOff,
            Boolean priorityForAccrual
    ) {}

    public record AllowedChanges(
            Boolean canSetWriteOff,
            Boolean canSetAccrual,
            List<String> reasons
    ) {}

    public record LastUpdated(
            LocalDateTime date,
            String by
    ) {}

    public record ErrorData(
            String code,
            String message,
            Object details,
            LocalDateTime timestamp
    ) {}

    public static AccountPriorityGetResponse success(AccountPriorityGetData data) {
        return new AccountPriorityGetResponse("SUCCESS", data, null);
    }

    public static AccountPriorityGetResponse error(String code, String message, Object details) {
        return new AccountPriorityGetResponse(
                "ERROR",
                null,
                new ErrorData(code, message, details, LocalDateTime.now())
        );
    }
}