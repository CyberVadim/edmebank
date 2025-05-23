package ru.edmebank.clients.utils.currency;

import com.github.petrovich4j.Gender;

public class RussianNumeralUtils {
    public static String declension(int number, String formZero, String[] forms) {
        int n = Math.abs(number) % 100;
        if (n >= 11 && n <= 19) {
            return forms[1];
        }
        int i = n % 10;
        return switch (i) {
            case 1 -> formZero;
            case 2, 3, 4 -> forms[0];
            default -> forms[1];
        };
    }

    public static String correctGender(String input, Gender gender) {
        return (gender == Gender.Female) ?
                input.replaceAll("\\bодин\\b$", "одна").replaceAll("\\bдва\\b$", "две") :
                input;
    }
}
