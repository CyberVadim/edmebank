package ru.edmebank.clients.fw;

public class Constants {
    private Constants() {}
    public static final String REGEX_PASSWORD_HAS_NUMBERS_AND_SMALL_AND_BIG_LETTERS = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$";
    public static final String REGEX_PASSWORD_HAS_SPECIAL_CHARACTERS = "^(?=.*[!#$%&'*+-/=?^_`{|}~.]).*$";
    public static final String REGEX_PASSWORD_ALLOW = "^[0-9a-zA-Z!#$%&'*+-/=?^_`{|}~.]{8,}$";
    public static final String REGEX_CARD_NUMBER = "^\\d{16,19}$";
    public static final String REGEX_EXPIRY_DATE = "^(0[1-9]|1[0-2])/([0-9]{2})$";
    public static final String REGEX_IBAN = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4,30}$";
    public static final String REGEX_CORRESPONDENT_ACCOUNT = "^301[0-9]{20}$";
    public static final String REGEX_FORBIDDEN_CHARS = "[<>\\|]";

    public static final String[] FORBIDDEN_PHRASES = {"шутка", "ха-ха", "блокируй", "тест", "прикол"};

    public static final int IBAN_MIN_LENGTH = 20;
    public static final int IBAN_MAX_LENGTH = 34;
}
