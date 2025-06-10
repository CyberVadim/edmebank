package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ExpiryDateValidatorTest {
    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidExpiryDate
        private String expiryDate;

        ValidationWrapper(String expiryDate) {
            this.expiryDate = expiryDate;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "01/25",
            "12/30"
    })
    void testValidExpiryDate(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "13/25",
            "301-12345678901234567890",
            "AAAA12123",
            "1/25",
            "123888888888",
            "01/1",
            "",
            "    "
    })
    void testInvalidExpiryDate(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullExpiryDate() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}