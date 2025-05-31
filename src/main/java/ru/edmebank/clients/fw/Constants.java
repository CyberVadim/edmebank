package ru.edmebank.clients.fw;

public class Constants {
    private Constants() {}
    public static final String REGEX_PASSWORD_HAS_NUMBERS_AND_SMALL_AND_BIG_LETTERS = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$";
    public static final String REGEX_PASSWORD_HAS_SPECIAL_CHARACTERS = "^(?=.*[!#$%&'*+-/=?^_`{|}~.]).*$";
    public static final String REGEX_PASSWORD_ALLOW = "^[0-9a-zA-Z!#$%&'*+-/=?^_`{|}~.]{8,}$";
}
