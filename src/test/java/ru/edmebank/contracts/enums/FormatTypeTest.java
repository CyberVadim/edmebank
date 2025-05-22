package ru.edmebank.contracts.enums;

import org.junit.jupiter.api.Test;
import ru.edmebank.clients.utils.currency.AmountParts;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FormatTypeTest {
    @Test
    void fullFormatDisplaysSpelledOutText() {
        String result = FormatType.FULL.format(AmountParts.from(new BigDecimal("4.25")), Currency.RUB);
        assertTrue(result.contains("(четыре)"));
        assertTrue(result.contains("руб"));
        assertTrue(result.contains("коп"));
    }

    @Test
    void standardFormatUsesCorrectDeclensions() {
        assertEquals("1 доллар 01 цент", FormatType.STANDARD.format(AmountParts.from(new BigDecimal("1.01")), Currency.USD));
        assertEquals("3 доллара 03 цента", FormatType.STANDARD.format(AmountParts.from(new BigDecimal("3.03")), Currency.USD));
        assertEquals("7 долларов 07 центов", FormatType.STANDARD.format(AmountParts.from(new BigDecimal("7.07")), Currency.USD));
    }

    @Test
    void shortFormatUsesAbbreviationsCorrectly() {
        assertEquals("12 руб. 34 коп.", FormatType.SHORT.format(AmountParts.from(new BigDecimal("12.34")), Currency.RUB));
        assertEquals("0 долл. 00 ¢", FormatType.SHORT.format(AmountParts.from(new BigDecimal("0.00")), Currency.USD));
    }
}