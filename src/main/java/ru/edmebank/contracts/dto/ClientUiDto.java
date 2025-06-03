package ru.edmebank.contracts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.edmebank.clients.domain.annotation.MinAge;
import ru.edmebank.clients.domain.annotation.Password;
import ru.edmebank.contracts.enums.AddressType;
import ru.edmebank.contracts.enums.ContactType;
import ru.edmebank.contracts.enums.Gender;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClientUiDto {

    @Valid
    private PersonalInfo personalInfo;
    @Valid
    private Passports passports;
    @Valid
    private List<Address> address;
    @Valid
    private List<Contact> contacts;

    @NotBlank
    @Password
    private String password;

    @Data
    public static class PersonalInfo {
        @Valid
        private FullName fullName;

        @NotNull(message = "Дата рождения обязательна")
        @JsonFormat(pattern = "dd.MM.yyyy")
        @MinAge
        private LocalDate birthDate;
        private Gender gender;
    }

    @Data
    public static class FullName {
        @NotBlank(message = "Фамилия не может быть пустой")
        @Pattern(
                regexp = "^[A-ZА-ЯЁ][a-zа-яё '-]+$",
                message = "Имя должно начинаться с заглавной буквы и может содержать буквы, пробелы, дефисы или апострофы")
        private String lastName;
        @NotBlank(message = "имя не может быть пустым")
        @Pattern(
                regexp = "^[A-ZА-ЯЁ][a-zа-яё '-]+$",
                message = "Фамилия должна начинаться с заглавной буквы и может содержать буквы, пробелы, дефисы или апострофы")
        private String firstName;
        @Pattern(
                regexp = "^[A-ZА-ЯЁ][a-zа-яё '-]+$",
                message = "Отчество должно начинаться с заглавной буквы и может содержать буквы, пробелы, дефисы или апострофы")
        private String middleName;
    }

    @Data
    public static class Passports {
        @Valid
        private Passport passport;
    }

    @Data
    public static class Passport {

        @NotBlank(message = "Серия обязательна")
        @Pattern(regexp = "^\\d{4}$",
                message = "Серия должна быть в формате XXXX")
        private String series;

        @NotBlank(message = "Номер обязателен")
        @Pattern(regexp = "^\\d{6}$",
                message = "Номер должен быть в формате ХХXXXX")
        private String number;

        @NotNull(message = "Дата обязательна")
        @JsonFormat(pattern = "dd.MM.yyyy")
        @Past(message = "Дата выдачи паспорта должна быть в прошедшем времени")
        private LocalDate issueDate;

        @NotBlank
        private String issuedBy;

        @NotBlank(message = "Код подразделения обязателен")
        @Pattern(regexp = "^\\d{3}-\\d{3}$",
                message = "Код подразделения должен быть в формате ХХX-XXX")
        private String departmentCode;
    }

    @Data
    public static class Address {

        private AddressType addressType;

        @NotBlank(message = "Индекс обязателен")
        @Pattern(regexp = "^\\d{6}$",
                message = "Индекс должен быть в формате ХХXXXX")
        private String postalCode;

        private String region;
        private String district;

        @NotBlank(message = "Пункт обязателен")
        private String city;
        private String street;

        @NotBlank(message = "Дом обязателен")
        private String house;
        private String building;
        private String corpus;
        private String apartment;
    }

    @Data
    @ru.edmebank.clients.domain.annotation.Contact
    public static class Contact {
        private ContactType type;
        private String value;
    }
}
