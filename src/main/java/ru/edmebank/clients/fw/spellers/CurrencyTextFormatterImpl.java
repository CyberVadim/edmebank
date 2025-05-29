package ru.edmebank.clients.fw.spellers;

import ru.edmebank.contracts.enums.Currency;

import java.math.BigDecimal;

import static ru.edmebank.contracts.enums.FormatType.FULL;
import static ru.edmebank.contracts.enums.FormatType.SHORT;
import static ru.edmebank.contracts.enums.FormatType.STANDARD;

public class CurrencyTextFormatterImpl implements CurrencyTextFormatter {
    @Override
    public String toFullForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, FULL);
    }

    @Override
    public String toStandardForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, STANDARD);
    }

    @Override
    public String toShortForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, SHORT);
    }
}