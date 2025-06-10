package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.FORBIDDEN_PHRASES;
import static ru.edmebank.clients.fw.Constants.REGEX_FORBIDDEN_CHARS;


class PaymentPurposeValidator implements ConstraintValidator<ValidPaymentPurpose, String> {
    private static final Pattern FORBIDDEN_CHARS_PATTERN = Pattern.compile(REGEX_FORBIDDEN_CHARS);
    private static final List<String> FORBIDDEN_PHRASES_LIST = List.of(FORBIDDEN_PHRASES);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return !containsForbiddenChars(value) && !containsForbiddenPhrases(value);
    }

    private boolean containsForbiddenChars(String value) {
        return FORBIDDEN_CHARS_PATTERN.matcher(value).find();
    }

    private boolean containsForbiddenPhrases(String value) {
        String lowerValue = value.toLowerCase();
        for (String phrase : FORBIDDEN_PHRASES_LIST) {
            if (lowerValue.contains(phrase.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
