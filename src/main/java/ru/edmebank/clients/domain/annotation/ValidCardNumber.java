package ru.edmebank.clients.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidator.class)
public @interface ValidCardNumber {
    String message() default "Некорректный номер карты - (16-19 цифр)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
