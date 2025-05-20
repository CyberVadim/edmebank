package ru.edmebank.clients.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.BeanWrapperImpl;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {

    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (fields.length == 0) return true;
        if (value == null) return false;

        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        boolean isValid = Stream.of(fields)
                .map(beanWrapper::getPropertyValue)
                .filter(Objects::nonNull)
                .anyMatch(this::isNotEmpty);

        if (!isValid) {
            String fieldsList = String.join(", ", fields);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate().replace("{fields}", fieldsList)
            ).addConstraintViolation();
        }

        return isValid;
    }

    private boolean isNotEmpty(Object fieldValue) {
        if (fieldValue == null) {
            return false;
        }
        if (fieldValue instanceof String str) {
            return !str.isEmpty();
        }
        if (fieldValue instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }

        return true;
    }
}