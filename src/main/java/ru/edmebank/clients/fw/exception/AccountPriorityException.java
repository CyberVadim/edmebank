package ru.edmebank.clients.fw.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountPriorityException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public AccountPriorityException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}