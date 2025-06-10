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
class CorrespondentAccountValidatorTest {
    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidCorrespondentAccount
        private String correspondentAccountNumber;

        ValidationWrapper(String correspondentAccountNumber) {
            this.correspondentAccountNumber = correspondentAccountNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "30100000000000000000000",
            "30199999999999999999999",
            "30112345678901234567890",
            "30101020304050607080900"
    })
    void testValidCorrespondentAccountCode(String number) {
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
    void testInvalidCorrespondentAccountCode(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullCorrespondentAccountCode() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}
