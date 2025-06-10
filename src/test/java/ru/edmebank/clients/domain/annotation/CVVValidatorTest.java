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
class CVVValidatorTest {
    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidCVV
        private String CVVNumber;

        ValidationWrapper(String CVVNumber) {
            this.CVVNumber = CVVNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "1234",
            "321",
            "4321"
    })
    void testValidCVVCode(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "30201020304050607080900",
            "301-12345678901234567890",
            "AAAA12123",
            "1.1",
            "123888888888",
            "4111111111111112",
            "",
            "    "
    })
    void testInvalidCVVCode(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullCVVCode() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}