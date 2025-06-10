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
class PaymentPurposeValidatorTest {
    @Autowired
    private Validator validator;

    private static class ValidationWrapper {
        @ValidPaymentPurpose
        private String paymentPurpose;

        ValidationWrapper(String paymentPurpose) {
            this.paymentPurpose = paymentPurpose;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Оплата услуг интернета",
            "Перевод средств",
            "Покупка товаров",
            "Аренда квартиры"
    })
    void testValidPaymentPurpose(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Оплата услуг <script>",
            "шутка про банк",
            "ха-ха, это тест",
            "блокируй карту!",
            "прикол|шутка",
            ">>>}|s<()",
            "    "
    })
    void testInvalidPaymentPurpose(String number) {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(number));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullPaymentPurpose() {
        Set<ConstraintViolation<ValidationWrapper>> violations =
                validator.validate(new ValidationWrapper(null));
        assertFalse(violations.isEmpty());
    }
}
