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
public class CardNumberValidatorTest {

    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidCardNumber
        private String cardNumber;

        ValidationWrapper(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "4532015112830366",
            "5500000000000004",
            "6011111111111117",
            "3566002020360505"
    })
    void testValidCardNumbers(String number) {
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
            "123123123",
            "123888888888",
            "4111111111111112",
            "",
            "    "
    })
    void testInvalidCardNumbers(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullCardNumber() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}