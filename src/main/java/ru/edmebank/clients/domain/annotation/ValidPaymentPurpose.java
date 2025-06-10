package ru.edmebank.clients.domain.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentPurposeValidator.class)
public @interface ValidPaymentPurpose {
    String message() default "Назначение платежа должно быть указано (не пустое)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
