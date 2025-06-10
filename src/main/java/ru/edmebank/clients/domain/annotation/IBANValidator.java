package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.IBAN_MAX_LENGTH;
import static ru.edmebank.clients.fw.Constants.IBAN_MIN_LENGTH;
import static ru.edmebank.clients.fw.Constants.REGEX_IBAN;

class IBANValidator implements ConstraintValidator<ValidIBAN, String> {
    private static final Pattern PATTERN = Pattern.compile(REGEX_IBAN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PATTERN.matcher(value).matches() && isLengthValid(value);
    }

    private boolean isLengthValid(String target) {
        return target.length() >= IBAN_MIN_LENGTH && target.length() <= IBAN_MAX_LENGTH;
    }
}
