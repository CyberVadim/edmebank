package ru.edmebank.clients.utils.currency;

import ru.edmebank.contracts.enums.Currency;
import ru.edmebank.contracts.enums.FormatType;

import java.math.BigDecimal;

public class CurrencyTextFormatterImpl implements CurrencyTextFormatter {

    @Override
    public String toFullForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, FormatType.FULL);
    }

    @Override
    public String toStandardForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, FormatType.STANDARD);
    }

    @Override
    public String toShortForm(BigDecimal amount, Currency currency) {
        return format(amount, currency, FormatType.SHORT);
    }
}