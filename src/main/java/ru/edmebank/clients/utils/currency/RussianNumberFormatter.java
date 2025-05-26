package ru.edmebank.clients.utils.currency;

import com.ibm.icu.text.RuleBasedNumberFormat;
import lombok.experimental.UtilityClass;

import java.util.Locale;

import static com.ibm.icu.text.RuleBasedNumberFormat.SPELLOUT;

@UtilityClass
public class RussianNumberFormatter {
    public static final Locale RUSSIAN = new Locale("ru");
    public static final RuleBasedNumberFormat SPELL_OUT = new RuleBasedNumberFormat(RUSSIAN, SPELLOUT);
}
