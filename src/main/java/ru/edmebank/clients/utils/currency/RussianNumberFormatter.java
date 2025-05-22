package ru.edmebank.clients.utils.currency;

import com.ibm.icu.text.RuleBasedNumberFormat;

import java.util.Locale;

public class RussianNumberFormatter {

    public static final Locale RUSSIAN = new Locale("ru");
    public static final RuleBasedNumberFormat SPELL_OUT = new RuleBasedNumberFormat(RUSSIAN, RuleBasedNumberFormat.SPELLOUT);
}
