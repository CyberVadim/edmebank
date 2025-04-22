package com.edmebank.clientmanagement.dto.spectrum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtAndSanctionRequest {

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустая")
    private String middleName;

    @NotBlank(message = "Отчество не может быть пустым")
    private String lastName;

    @NotBlank(message = "Дата рождения не может быть пустой")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Дата рождения должна быть в формате ДД-ММ-ГГГГ")
    private String birthDate;
}
