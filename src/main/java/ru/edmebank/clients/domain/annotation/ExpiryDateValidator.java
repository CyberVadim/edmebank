package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.REGEX_EXPIRY_DATE;

class ExpiryDateValidator implements ConstraintValidator<ValidExpiryDate, String> {
    private static final Pattern PATTERN = Pattern.compile(REGEX_EXPIRY_DATE);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PATTERN.matcher(value).matches();
    }
}
