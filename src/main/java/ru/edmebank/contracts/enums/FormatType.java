package ru.edmebank.contracts.enums;

import ru.edmebank.clients.utils.currency.AmountParts;

import static java.lang.String.format;
import static ru.edmebank.clients.utils.currency.RussianNumberFormatter.SPELL_OUT;
import static ru.edmebank.clients.utils.currency.RussianNumeralUtils.correctGender;
import static ru.edmebank.clients.utils.currency.RussianNumeralUtils.declension;

public enum FormatType {
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
