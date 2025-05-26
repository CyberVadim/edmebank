package ru.edmebank.clients.utils.currency;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;

public record AmountParts(String sign, int whole, int fractional) {
    public static AmountParts from(BigDecimal amount) {
        String sign = amount.signum() < 0 ? "-" : "";
        BigDecimal abs = amount.abs();
        int whole = abs.intValue();
        int fractional = abs.remainder(ONE).movePointRight(2).intValue();
        return new AmountParts(sign, whole, fractional);
    }
}
