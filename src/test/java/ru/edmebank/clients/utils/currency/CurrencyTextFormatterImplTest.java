package ru.edmebank.clients.utils.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.edmebank.contracts.enums.Currency;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyTextFormatterImplTest {
    private CurrencyTextFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new CurrencyTextFormatterImpl();
    }

    @Test
    void fullFormHandlesSingleUnit() {
        String expected = "101 (сто один) рубль 01 (одна) копейка";
        String result = formatter.toFullForm(new BigDecimal("101.01"), Currency.RUB);
        assertEquals(expected, result);
    }

    @Test
    void fullFormHandlesSingleUnitTwo() {
        String expected = "101 (сто один) рубль 02 (две) копейки";
        String result = formatter.toFullForm(new BigDecimal("101.02"), Currency.RUB);
        assertEquals(expected, result);
    }

    @Test
    void fullFormHandlesPlural234() {
        String expected = "3 (три) доллара 02 (два) цента";
        String result = formatter.toFullForm(new BigDecimal("3.02"), Currency.USD);
        assertEquals(expected, result);
    }

    @Test
    void fullFormHandlesPluralMany() {
        String expected = "5 (пять) рублей 05 (пять) копеек";
        String result = formatter.toFullForm(new BigDecimal("5.05"), Currency.RUB);
        assertEquals(expected, result);
    }

    @Test
    void fullFormHandlesZeroWholeAndFraction() {
        String expected = "0 (ноль) долларов 00 (ноль) центов";
        String result = formatter.toFullForm(new BigDecimal("0.00"), Currency.USD);
        assertEquals(expected, result);
    }

    @Test
    void standardFormWithAllVariants() {
        assertEquals("101 рубль 01 копейка", formatter.toStandardForm(new BigDecimal("101.01"), Currency.RUB));
        assertEquals("202 рубля 02 копейки", formatter.toStandardForm(new BigDecimal("202.02"), Currency.RUB));
        assertEquals("505 рублей 05 копеек", formatter.toStandardForm(new BigDecimal("505.05"), Currency.RUB));
    }

    @Test
    void shortFormShouldUseAbbreviations() {
        assertEquals("12 долл. 34 ¢", formatter.toShortForm(new BigDecimal("12.34"), Currency.USD));
    }

    @Test
    void negativeAmountFormatsCorrectly() {
        assertEquals("-3 рубля 10 копеек", formatter.toStandardForm(new BigDecimal("-3.10"), Currency.RUB));
    }

    @Test
    void roundingTruncatesFractionCorrectly() {
        assertEquals("2 долл. 99 ¢", formatter.toShortForm(new BigDecimal("2.999"), Currency.USD));
    }

    @Test
    void largeAmountFormatsCorrectly() {
        assertEquals("1234567 долларов 89 центов", formatter.toStandardForm(new BigDecimal("1234567.89"), Currency.USD));
    }
}