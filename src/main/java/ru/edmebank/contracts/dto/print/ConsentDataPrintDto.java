package ru.edmebank.contracts.dto.print;

public record ConsentDataPrintDto(
        String firstName,
        String lastName,
        String middleName,
        String birthDate,
        String email
) {}

