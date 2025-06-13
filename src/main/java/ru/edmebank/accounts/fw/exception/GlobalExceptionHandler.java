package ru.edmebank.accounts.fw.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<AccountPriorityResponse> handleAccountException(AccountException ex) {
        log.error("Обработка AccountException: {}", ex.getMessage(), ex);

        AccountPriorityResponse response = AccountPriorityResponse.error(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getDetails()
        );

        HttpStatus status = getHttpStatusByErrorCode(ex.getErrorCode());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AccountPriorityResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Ошибка валидации запроса: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        AccountPriorityResponse response = AccountPriorityResponse.error(
                "INVALID_REQUEST",
                "Неверный формат запроса",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AccountPriorityResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Ошибка валидации запроса: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });

        AccountPriorityResponse response = AccountPriorityResponse.error(
                "INVALID_REQUEST",
                "Неверный формат запроса",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AccountPriorityResponse> handleGenericException(Exception ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);

        AccountPriorityResponse response = AccountPriorityResponse.error(
                "INTERNAL_ERROR",
                "Внутренняя ошибка сервера",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus getHttpStatusByErrorCode(String errorCode) {
        return switch (errorCode) {
            case "INVALID_REQUEST" -> HttpStatus.BAD_REQUEST;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "ACCOUNT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "CONFLICT_PRIORITIES" -> HttpStatus.CONFLICT;
            case "INVALID_ACCOUNT_STATE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "TOO_MANY_REQUESTS" -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}