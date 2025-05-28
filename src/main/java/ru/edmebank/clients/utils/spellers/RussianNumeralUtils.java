package ru.edmebank.clients.utils.spellers;

import com.github.petrovich4j.Gender;
import lombok.experimental.UtilityClass;

import static com.github.petrovich4j.Gender.Female;
import static java.lang.Math.abs;

/**
 * Утилитный класс для работы с русскими числительными.
 * Предоставляет методы для склонения чисел и корректировки рода числительных.
 */
@UtilityClass
public class RussianNumeralUtils {
    /**
     * Определяет правильную форму числительного в зависимости от числа.
     * Учитываются особенности склонения числительных в русском языке.
     *
     * @param number   число
     * @param formZero форма числительного для единичных значений (например, "один")
     * @param forms    массив форм числительного для нескольких вариантов (например, ["два", "два", "два"])
     * @return строка, представляющая правильную форму числительного
     */
    public static String declension(int number, String formZero, String[] forms) {
        int n = abs(number) % 100;

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

    /**
     * Исправляет род числительного в зависимости от пола.
     * Например, для женского рода заменяются "один" на "одна" и "два" на "две".
     *
     * @param input  строка с числительным
     * @param gender пол (мужской или женский)
     * @return строка с числительным, исправленным в соответствии с полом
     */
    public static String correctGender(String input, Gender gender) {
        return (gender == Female) ?
                input.replaceAll("\\bодин\\b$", "одна").replaceAll("\\bдва\\b$", "две") :
                input;
    }
}
