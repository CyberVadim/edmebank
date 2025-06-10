package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.REGEX_CONFIRMATION_CODE;

class ConfirmationCodeValidator implements ConstraintValidator<ValidConfirmationCode, String> {
    private static final Pattern PATTERN = Pattern.compile(REGEX_CONFIRMATION_CODE);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PATTERN.matcher(value).matches();
    }
}
