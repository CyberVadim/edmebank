package ru.edmebank.clients.fw.spellers;

import ru.edmebank.contracts.enums.Currency;
import ru.edmebank.contracts.enums.FormatType;

import java.math.BigDecimal;

import static ru.edmebank.clients.fw.spellers.AmountParts.from;

public interface CurrencyTextFormatter {
    String toFullForm(BigDecimal amount, Currency currency);

    String toStandardForm(BigDecimal amount, Currency currency);

    String toShortForm(BigDecimal amount, Currency currency);

    default String format(BigDecimal amount, Currency currency, FormatType type) {
        AmountParts parts = from(amount);
        return type.formatter(parts, currency);
    }
}
