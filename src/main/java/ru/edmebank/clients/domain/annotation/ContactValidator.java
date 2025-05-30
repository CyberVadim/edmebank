package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.edmebank.contracts.dto.ClientUiDto;

public class ContactValidator implements ConstraintValidator<ContactValid, ClientUiDto.Contact> {

    @Override
    public boolean isValid(ClientUiDto.Contact contact, ConstraintValidatorContext context) {
        if (contact.getType() == null || contact.getValue() == null) {
            return false;
        }
        return contact.getType().getPattern().matcher(contact.getValue()).matches();
    }
}
