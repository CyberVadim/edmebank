package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.edmebank.contracts.dto.ClientUiDto;

import java.util.regex.Pattern;

public class ContactValidator implements ConstraintValidator<ContactValid, ClientUiDto.Contact> {

    // Регулярные выражения
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^+7\\([0-9]{3}\\)[0-9]{3}-[0-9]{2}-[0-9]{2}$");

    @Override
    public boolean isValid(ClientUiDto.Contact contact, ConstraintValidatorContext context) {
        if (contact.getType() == null || contact.getValue() == null) {
            return false;
        }

        switch (contact.getType()) {
            case EMAIL:
                return EMAIL_PATTERN.matcher(contact.getValue()).matches();
            case PHONE:
                return PHONE_PATTERN.matcher(contact.getValue()).matches();
            default:
                return false;
        }
    }

}
