package com.edmebank.clientmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String passportNumber;
    private String passportSeries;
    private String passportIssuedBy;
    private LocalDate passportIssueDate;
    private String address;
    @Pattern(
            regexp = "\\+7\\s?\\d{3}\\s?\\d{3}-?\\d{2}-?\\d{2}",
            message = "Номер телефона должен быть в формате +7 900 123-45-67"
    )
    private String phone;
    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email обязателен")
    private String email;
    private String inn;
    private String snils;
    private boolean amlChecked;
}
