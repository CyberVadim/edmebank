package ru.edmebank.clients.fw.spellers;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;

/**
 * Представляет части суммы в виде целого числа и дробной части.
 * Содержит знак, целую и дробную части числа.
 */
public record AmountParts(String sign, int whole, int fractional) {
    /**
     * Преобразует число типа BigDecimal в представление частей суммы.
     *
     * @param amount сумма типа BigDecimal
     * @return объект AmountParts, содержащий знак, целую и дробную части суммы
     */
    public static AmountParts from(BigDecimal amount) {
        String sign = amount.signum() < 0 ? "-" : "";
        BigDecimal abs = amount.abs();
        int whole = abs.intValue();
        int fractional = abs.remainder(ONE).movePointRight(2).intValue();
        return new AmountParts(sign, whole, fractional);
    }
}
