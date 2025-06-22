package ru.edmebank.accounts.fw.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class AccountExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {
        log.error("Account not found exception: {}", ex.getMessage());

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode("ACCOUNT_NOT_FOUND");
        errorData.setMessage(ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (ex.getMessage().contains(": ")) {
            details.put("accountId", ex.getMessage().split(": ")[1]);
        } else {
            details.put("accountId", "unknown");
        }
        errorData.setDetails(details);

        errorData.setTimestamp(ZonedDateTime.now());

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountValidationException.class)
    public ResponseEntity<Object> handleAccountValidationException(AccountValidationException ex) {
        log.error("Account validation exception: {}, code: {}", ex.getMessage(), ex.getErrorCode());

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode(ex.getErrorCode());
        errorData.setMessage(ex.getMessage());
        errorData.setTimestamp(ZonedDateTime.now());

        Map<String, Object> details = ex.getDetails() != null ? ex.getDetails() : new HashMap<>();
        errorData.setDetails(details);

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        HttpStatus status;
        switch (ex.getErrorCode()) {
            case "INVALID_REQUEST":
                status = HttpStatus.BAD_REQUEST;
                break;
            case "CONFLICT_PRIORITIES":
                status = HttpStatus.CONFLICT;
                break;
            case "INVALID_ACCOUNT_STATE":
            case "ACCOUNT_BLOCKED":
                status = HttpStatus.UNPROCESSABLE_ENTITY;
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage());

        Map<String, String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing + "; " + replacement
                ));

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode("INVALID_REQUEST");
        errorData.setMessage("Неверный формат запроса");
        errorData.setTimestamp(ZonedDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("validationErrors", errors);
        errorData.setDetails(details);

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied exception: {}", ex.getMessage());

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode("FORBIDDEN");
        errorData.setMessage("Доступ запрещен");
        errorData.setTimestamp(ZonedDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("message", "У пользователя нет прав на изменение приоритетов");
        errorData.setDetails(details);

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsException ex) {
        log.error("Too many requests exception: {}", ex.getMessage());

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode("TOO_MANY_REQUESTS");
        errorData.setMessage(ex.getMessage());
        errorData.setTimestamp(ZonedDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("retryAfterSeconds", 60);
        errorData.setDetails(details);

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Unexpected exception: ", ex);

        AccountPriorityResponse.ErrorData errorData = new AccountPriorityResponse.ErrorData();
        errorData.setCode("INTERNAL_ERROR");
        errorData.setMessage("Внутренняя ошибка сервера");
        errorData.setTimestamp(ZonedDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("exceptionType", ex.getClass().getName());
        details.put("exceptionMessage", ex.getMessage());
        errorData.setDetails(details);

        AccountPriorityResponse response = new AccountPriorityResponse();
        response.setStatus("ERROR");
        response.setError(errorData);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}