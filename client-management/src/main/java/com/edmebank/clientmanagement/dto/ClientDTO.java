package com.edmebank.clientmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для клиента")
public class ClientDTO {

    @Schema(description = "Идентификатор клиента", example = "b3d8b938-6e84-49f6-a3ec-79830946b73a", hidden = true)
    private UUID id;

    @Schema(description = "Имя", example = "Юлия")
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @Schema(description = "Фамилия", example = "Халабудина")
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Schema(description = "Отчество", example = "Алексеевна")
    private String middleName;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate dateOfBirth;

    @Schema(description = "Серия паспорта", example = "6606")
    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна состоять из 4 цифр")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "304001")
    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен состоять из 6 цифр")
    private String passportNumber;

    @Schema(description = "Кем выдан паспорт", example = "УФМС России по г. Москве")
    private String passportIssuedBy;

    @Schema(description = "Дата выдачи паспорта", example = "2010-05-15")
    private LocalDate passportIssueDate;

    @Schema(description = "Адрес проживания", example = "г. Москва, ул. Ленина, д.1, кв.1")
    private String address;

    @Schema(description = "Номер телефона", example = "+7 900 123-45-67")
    @Pattern(
            regexp = "\\+7\\s?\\d{3}\\s?\\d{3}-?\\d{2}-?\\d{2}",
            message = "Номер телефона должен быть в формате +7 900 123-45-67"
    )
    private String phone;

    @Schema(description = "Email", example = "ivanov@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неверный формат email"
    )
    private String email;

    @Schema(description = "ИНН", example = "056105627867")
    @Pattern(regexp = "\\d{12}", message = "ИНН должен состоять из 12 цифр")
    private String inn;

    @Schema(description = "СНИЛС", example = "112-233-445 95")
    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{3} \\d{2}", message = "СНИЛС должен быть в формате 123-456-789 01")
    private String snils;

    @Schema(description = "Флаг проверки AML", example = "true", hidden = true)
    private boolean amlChecked;
}
