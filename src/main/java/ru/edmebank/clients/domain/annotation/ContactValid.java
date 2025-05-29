package ru.edmebank.clients.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContactValidator.class)
public @interface ContactValid {
    String message() default "Некорректные контактные данные";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
