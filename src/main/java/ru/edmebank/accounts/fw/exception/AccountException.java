package ru.edmebank.accounts.fw.exception;

import lombok.Getter;

@Getter
public class AccountException extends RuntimeException {

    private final String errorCode;
    private final Object details;

    public AccountException(String message, String errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public AccountException(String message, String errorCode) {
        this(message, errorCode, null);
    }
}