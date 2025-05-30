package ru.edmebank.clients.app.impl.service.generatedDto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!#$%&'*+-/=?^_`{|}~.";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateValidPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Пароль должен быть не короче 8 символов");
        }

        StringBuilder password = new StringBuilder(length);

        // Гарантируем наличие обязательных символов:
        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        // Остальные символы случайные из всех допустимых
        for (int i = 4; i < length; i++) {
            password.append(randomChar(ALL));
        }

        // Перемешаем символы
        return shuffle(password.toString());
    }

    private static char randomChar(String chars) {
        return chars.charAt(RANDOM.nextInt(chars.length()));
    }

    private static String shuffle(String input) {
        List<Character> chars = new ArrayList<>(input.chars()
                .mapToObj(c -> (char) c)
                .toList());
        java.util.Collections.shuffle(chars);
        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) shuffled.append(c);
        return shuffled.toString();
    }
}
