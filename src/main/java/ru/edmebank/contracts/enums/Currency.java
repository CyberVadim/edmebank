package ru.edmebank.contracts.enums;

import lombok.AllArgsConstructor;
import com.github.petrovich4j.Gender;

@AllArgsConstructor
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
}
