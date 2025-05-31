package ru.edmebank.clients.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static ru.edmebank.clients.fw.Constants.REGEX_PASSWORD_ALLOW;
import static ru.edmebank.clients.fw.Constants.REGEX_PASSWORD_HAS_NUMBERS_AND_SMALL_AND_BIG_LETTERS;
import static ru.edmebank.clients.fw.Constants.REGEX_PASSWORD_HAS_SPECIAL_CHARACTERS;

public class PasswordValidator implements ConstraintValidator<Password, String> {


    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {

        if (s.contains("\\s")) {
            setContext(context, "Пароль не должен содержать пробелные символы.");
            return false;
        }
        if (s.length() < 8) {
            setContext(context, "Пароль должен состоять из 8 или более символов.");
            return false;
        }
        if (!Pattern.compile(REGEX_PASSWORD_HAS_NUMBERS_AND_SMALL_AND_BIG_LETTERS).matcher(s).matches()) {
            setContext(context, "Пароль должен содержать цифры, маленькие и большие латинские буквы.");
            return false;
        }
        if (!Pattern.compile(REGEX_PASSWORD_HAS_SPECIAL_CHARACTERS).matcher(s).matches()) {
            setContext(context, "Пароль должен содержать хотя бы один спецсимвол !#$%&'*+-/=?^_`{|}~.");
            return false;
        }
        if (!Pattern.compile(REGEX_PASSWORD_ALLOW).matcher(s).matches()) {
            setContext(context, "Пароль содержит недопустимые символы.");
            return false;
        }
        return true;
    }

    private void setContext(ConstraintValidatorContext context, String resultValidate) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(resultValidate).addConstraintViolation();
    }
}
