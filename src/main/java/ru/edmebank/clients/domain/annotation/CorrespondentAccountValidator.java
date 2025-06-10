package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.REGEX_CORRESPONDENT_ACCOUNT;

class CorrespondentAccountValidator implements ConstraintValidator<ValidCorrespondentAccount, String> {
    private static final Pattern PATTERN = Pattern.compile(REGEX_CORRESPONDENT_ACCOUNT);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PATTERN.matcher(value).matches();
    }
}
