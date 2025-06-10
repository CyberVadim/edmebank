package ru.edmebank.clients.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface ValidAccountNumber {
    String message() default "Некорректный расчётный счёт - (20 цифр)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
