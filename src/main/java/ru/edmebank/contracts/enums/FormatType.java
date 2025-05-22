package ru.edmebank.contracts.enums;

import ru.edmebank.clients.utils.currency.AmountParts;
import ru.edmebank.clients.utils.currency.RussianNumeralUtils;

import static ru.edmebank.clients.utils.currency.RussianNumberFormatter.SPELL_OUT;

public enum FormatType {
    FULL {
        @Override
        public String format(AmountParts parts, Currency currency) {
            return String.format("%s%d (%s) %s %02d (%s) %s",
                    parts.sign(),
                    parts.whole(),
                    RussianNumeralUtils.correctGender(SPELL_OUT.format(parts.whole()), currency.wholeGender),
                    RussianNumeralUtils.declension(parts.whole(), currency.wholeText, currency.wholeForms),
                    parts.fractional(),
                    RussianNumeralUtils.correctGender(SPELL_OUT.format(parts.fractional()), currency.fractionalGender),
                    RussianNumeralUtils.declension(parts.fractional(), currency.fractionalText, currency.fractionalForms));
        }
    },
    STANDARD {
        @Override
        public String format(AmountParts parts, Currency currency) {
            return String.format("%s%d %s %02d %s",
                    parts.sign(),
                    parts.whole(),
                    RussianNumeralUtils.declension(parts.whole(), currency.wholeText, currency.wholeForms),
                    parts.fractional(),
                    RussianNumeralUtils.declension(parts.fractional(), currency.fractionalText, currency.fractionalForms));
        }
    },
    SHORT {
        @Override
        public String format(AmountParts parts, Currency currency) {
            return String.format("%s%d %s %02d %s",
                    parts.sign(),
                    parts.whole(),
                    currency.wholeShort,
                    parts.fractional(),
                    currency.fractionalShort);
        }
    };

    public abstract String format(AmountParts parts, Currency currency);
}
