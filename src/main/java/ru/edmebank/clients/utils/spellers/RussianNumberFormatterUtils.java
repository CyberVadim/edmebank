package ru.edmebank.clients.utils.spellers;

import com.ibm.icu.text.RuleBasedNumberFormat;
import lombok.experimental.UtilityClass;

import java.util.Locale;

import static com.ibm.icu.text.RuleBasedNumberFormat.SPELLOUT;

/**
 * Утилитный класс для форматирования чисел на русском языке.
 * Предоставляет методы для представления чисел в текстовой форме.
 */
@UtilityClass
public class RussianNumberFormatterUtils {
    public static final Locale RUSSIAN = new Locale("ru");

    /**
     * Правила форматирования чисел в текстовую форму на русском языке.
     * Используется для преобразования чисел в их текстовые представления (например, 123 → "сто двадцать три").
     */
    public static final RuleBasedNumberFormat SPELL_OUT = new RuleBasedNumberFormat(RUSSIAN, SPELLOUT);
}
