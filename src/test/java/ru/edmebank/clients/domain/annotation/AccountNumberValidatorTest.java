package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccountNumberValidatorTest {

    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidAccountNumber
        private String accountNumber;

        ValidationWrapper(String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "22345678901234567890",
            "32345678901234567890",
            "42345678901234567890",
            "52345678901234567890"
    })
    void testValidAccountNumbers(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "pivo",
            "22345678901234567890  ",
            "\n22345678901234567890",
            "123123123",
            "123888888888",
            "4111111111111112",
            "",
            "    "
    })
    void testInvalidAccountNumbers(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullAccountNumber() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}
