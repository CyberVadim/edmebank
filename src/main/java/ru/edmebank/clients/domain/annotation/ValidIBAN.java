package ru.edmebank.clients.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IBANValidator.class)
public @interface ValidIBAN {
    String message() default "Некорректный IBAN - (RUXX XXXX XXXX XXXX, 20-34 символа)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
