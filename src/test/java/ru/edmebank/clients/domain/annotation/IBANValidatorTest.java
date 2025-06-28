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
public class IBANValidatorTest {
    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidIBAN
        private String IBANNumber;

        ValidationWrapper(String IBANNumber) {
            this.IBANNumber = IBANNumber;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GH01A1B2C3D4E5F6G7H8",
            "ZZ99ZZZZ9999ZZZZ9999ZZZZ9999ZZ"
    })
    void testValidIBANNumber(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "aaaaaaaa-aasdasd",
            "301-12345678901234567890",
            "AAAA12123",
            "11",
            "123888888888",
            "14421242144214214ZXXXAx",
            "",
            "    "
    })
    void testInvalidIBANNumber(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullIBANNumber() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}