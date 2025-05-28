package ru.edmebank.clients.fw.spellers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;
import static ru.edmebank.clients.fw.spellers.AmountParts.from;

class AmountPartsTest {
    @ParameterizedTest(name = "[{index}] {0} → sign: \"{1}\", whole: {2}, fractional: {3}")
    @MethodSource("validAmounts")
    void testFrom(BigDecimal input, String expectedSign, int expectedWhole, int expectedFractional) {
        AmountParts result = from(input);
        assertEquals(expectedSign, result.sign());
        assertEquals(expectedWhole, result.whole());
        assertEquals(expectedFractional, result.fractional());
    }

    static Stream<Arguments> validAmounts() {
        return Stream.of(
                arg("0", "", 0, 0),
                arg("12", "", 12, 0),
                arg("12.00", "", 12, 0),
                arg("12.01", "", 12, 1),
                arg("12.99", "", 12, 99),
                arg("12.999", "", 12, 99),
                arg("12.009", "", 12, 0),
                arg("0.10", "", 0, 10),
                arg("0.01", "", 0, 1),
                arg("0.001", "", 0, 0),

                arg("-1", "-", 1, 0),
                arg("-1.00", "-", 1, 0),
                arg("-1.01", "-", 1, 1),
                arg("-1.99", "-", 1, 99),
                arg("-0.01", "-", 0, 1),
                arg("-0.00", "", 0, 0),
                arg("-12.999", "-", 12, 99),

                arg("123456789.987", "", 123456789, 98),
                arg("-123456789.999", "-", 123456789, 99)
        );
    }

    @Test
    void testImmutability() {
        AmountParts original = from(new BigDecimal("42.58"));
        assertEquals("AmountParts[sign=, whole=42, fractional=58]", original.toString());
    }

    private static Arguments arg(String input, String sign, int whole, int fractional) {
        return of(new BigDecimal(input), sign, whole, fractional);
    }
}
