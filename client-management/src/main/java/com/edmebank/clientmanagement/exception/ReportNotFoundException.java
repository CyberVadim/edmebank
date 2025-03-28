package com.edmebank.clientmanagement.exception;

import jakarta.validation.constraints.NotBlank;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message, @NotBlank(message = "Фамилия обязательна") String lastName, @NotBlank(message = "Имя обязательно") String firstName) {
        super(message);
    }
}
