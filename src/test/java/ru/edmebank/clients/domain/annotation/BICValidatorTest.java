package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BICValidatorTest {

    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidBIC
        private String bicNumber;

        ValidationWrapper(String bicNumber) {
            this.bicNumber = bicNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789",
            "223456789",
            "323456789",
            "423456789"
    })
    void testValidBICNumbers(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "null",
            "AAAA12123",
            "1.1",
            "123888888888",
            "4111111111111112",
            "",
            "    "
    })
    void testInvalidBICNumbers(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullBICNumber() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}