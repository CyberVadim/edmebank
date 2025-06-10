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
class ConfirmationCodeValidatorTest {

    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidConfirmationCode
        private String codeNumber;

        ValidationWrapper(String codeNumber) {
            this.codeNumber = codeNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456",
            "111111",
            "000000",
            "999999"
    })
    void testValidConfirmationCode(String number) {
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
    void testInvalidConfirmationCode(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullConfirmationCode() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}
