package ru.edmebank.accounts.fw.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "ru.edmebank.accounts")
@Order(1)
@Slf4j
public class AccountsGlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<AccountPriorityResponse> handleAccountException(AccountException ex) {
        log.error("Обработка AccountException: {}", ex.getMessage());

        String errorCode = ex.getMessage();
        String errorMessage = ex.getErrorCode();
        HttpStatus status = determineHttpStatus(errorCode);

        return ResponseEntity
                .status(status)
                .body(AccountPriorityResponse.error(errorCode, errorMessage, ex.getDetails()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AccountPriorityResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Ошибка валидации запроса: {}", ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(AccountPriorityResponse.error("INVALID_REQUEST", "Неверный формат запроса", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AccountPriorityResponse> handleGenericException(Exception ex) {
        log.error("Необработанное исключение: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AccountPriorityResponse.error("INTERNAL_ERROR", "Внутренняя ошибка сервера", ex.getMessage()));
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "ACCOUNT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INVALID_REQUEST" -> HttpStatus.BAD_REQUEST;
            case "CONFLICT_PRIORITIES" -> HttpStatus.CONFLICT;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "INVALID_ACCOUNT_STATE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}