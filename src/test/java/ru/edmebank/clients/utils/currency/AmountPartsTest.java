package ru.edmebank.clients.utils.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmountPartsTest {
    @Test
    void shouldExtractWholeAndFractional() {
        AmountParts parts = AmountParts.from(new BigDecimal("99.87"));
        assertEquals(99, parts.whole());
        assertEquals(87, parts.fractional());
    }

    @Test
    void shouldExtractZeroFraction() {
        AmountParts parts = AmountParts.from(new BigDecimal("10.00"));
        assertEquals(10, parts.whole());
        assertEquals(0, parts.fractional());
    }

    @Test
    void shouldExtractNegativeValuesCorrectly() {
        AmountParts parts = AmountParts.from(new BigDecimal("-4.50"));
        assertEquals(-4, parts.whole());
        assertEquals(50, parts.fractional());
    }

    @Test
    void shouldTruncateFractionWithoutRounding() {
        AmountParts parts = AmountParts.from(new BigDecimal("3.999"));
        assertEquals(3, parts.whole());
        assertEquals(99, parts.fractional());
    }

    @Test
    void shouldHandleLargeNumbers() {
        AmountParts parts = AmountParts.from(new BigDecimal("123456789.01"));
        assertEquals(123456789, parts.whole());
        assertEquals(1, parts.fractional());
    }
}
