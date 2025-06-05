package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    private int minAge;

    @Override
    public void initialize(MinAge constraintAnnotation) {
        this.minAge = constraintAnnotation.value(); // Получаем минимальный возраст из аннотации
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        return Optional.ofNullable(birthDate)
                .map(date -> Period.between(date, LocalDate.now()).getYears() >= minAge)
                .orElse(false);
    }
}
