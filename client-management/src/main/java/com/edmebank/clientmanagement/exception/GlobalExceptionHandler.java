package com.edmebank.clientmanagement.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleClientNotFoundException(ClientNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleClientAlreadyExistsException(ClientAlreadyExistsException ex) {
        return ex.getMessage();
    }

    // Обработка ошибок валидации данных
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    // Обработка ошибки дублирования email
    @ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleDuplicateEmailException(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Этот email уже зарегистрирован в системе.");
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPassportException.class)
    public Map<String, String> handleInvalidPassportException(InvalidPassportException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AmlCheckedException.class)
    public ResponseEntity<Map<String, String>> handleTerroristFoundException(AmlCheckedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(VpnActiveException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleVpnActiveException(VpnActiveException e) {
        return e.getMessage();
    }
}
