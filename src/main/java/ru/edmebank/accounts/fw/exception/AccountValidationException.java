package ru.edmebank.accounts.fw.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class AccountValidationException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;

    public AccountValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public AccountValidationException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}