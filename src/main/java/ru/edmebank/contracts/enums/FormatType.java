package ru.edmebank.contracts.enums;

import ru.edmebank.clients.fw.spellers.AmountParts;

import static java.lang.String.format;
import static ru.edmebank.clients.utils.spellers.RussianNumberFormatterUtils.SPELL_OUT;
import static ru.edmebank.clients.utils.spellers.RussianNumeralUtils.correctGender;
import static ru.edmebank.clients.utils.spellers.RussianNumeralUtils.declension;

/**
 * Перечисление, представляющее различные форматы для форматирования суммы с валютой.
 * Каждый формат имеет свою логику форматирования, учитывая целые и дробные части, склонения и род.
 */
public enum FormatType {
    /**
     * Полный формат, включающий знак, полное числовое значение, склонения, и текстовые представления целой и дробной части.
     */
    FULL {
        @Override
        public String formatter(AmountParts parts, Currency currency) {
            return format("%s%d (%s) %s %02d (%s) %s",
                    safeString(parts.sign()),
                    parts.whole(),
                    safeString(correctGender(SPELL_OUT.format(parts.whole()), currency.wholeGender)),
                    safeString(declension(parts.whole(), currency.wholeText, currency.wholeForms)),
                    parts.fractional(),
                    safeString(correctGender(SPELL_OUT.format(parts.fractional()), currency.fractionalGender)),
                    safeString(declension(parts.fractional(), currency.fractionalText, currency.fractionalForms)));
        }
    },

    /**
     * Стандартный формат, включающий знак, числовые значения целой и дробной частей и склонения.
     */
    STANDARD {
        @Override
        public String formatter(AmountParts parts, Currency currency) {
            return format("%s%d %s %02d %s",
                    safeString(parts.sign()),
                    parts.whole(),
                    safeString(declension(parts.whole(), currency.wholeText, currency.wholeForms)),
                    parts.fractional(),
                    safeString(declension(parts.fractional(), currency.fractionalText, currency.fractionalForms)));
        }
    },

    /**
     * Сокращенный формат, включающий знак, числовые значения целой и дробной частей и их короткие формы.
     */
    SHORT {
        @Override
        public String formatter(AmountParts parts, Currency currency) {
            return format("%s%d %s %02d %s",
                    safeString(parts.sign()),
                    parts.whole(),
                    safeString(currency.wholeShort),
                    parts.fractional(),
                    safeString(currency.fractionalShort));
        }
    };

    public abstract String formatter(AmountParts parts, Currency currency);

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}
