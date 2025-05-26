package ru.edmebank.contracts.enums;

import lombok.AllArgsConstructor;
import com.github.petrovich4j.Gender;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Currency {
    RUB("рубль", "копейка",
            "руб.", "коп.",
            new String[]{"рубля", "рублей"}, new String[]{"копейки", "копеек"},
            Gender.Male, Gender.Female),
    USD("доллар", "цент",
            "долл.", "¢",
            new String[]{"доллара", "долларов"}, new String[]{"цента", "центов"},
            Gender.Male, Gender.Male);

    public final String wholeText;
    public final String fractionalText;
    public final String wholeShort;
    public final String fractionalShort;
    public final String[] wholeForms;
    public final String[] fractionalForms;
    public final Gender wholeGender;
    public final Gender fractionalGender;

    private static Currency findByField(String value, FieldSelector selector) {
        for (Currency currency : values()) {
            if (selector.select(currency).equalsIgnoreCase(value)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Не найдена валюта для значения: " + value);
    }

    private interface FieldSelector {
        String select(Currency currency);
    }

    public static Currency fromWholeText(String wholeText) {
        return findByField(wholeText, Currency::getWholeText);
    }

    public static Currency fromWholeShort(String wholeShort) {
        return findByField(wholeShort, Currency::getWholeShort);
    }

    public static Currency fromFractionalShort(String fractionalShort) {
        return findByField(fractionalShort, Currency::getFractionalShort);
    }

    public static Currency fromFractionalText(String fractionalText) {
        return findByField(fractionalText, Currency::getFractionalText);
    }
}
