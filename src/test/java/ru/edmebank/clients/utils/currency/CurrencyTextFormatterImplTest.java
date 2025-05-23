package ru.edmebank.clients.utils.currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.edmebank.contracts.enums.Currency;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyTextFormatterImplTest {

    private final CurrencyTextFormatterImpl formatter = new CurrencyTextFormatterImpl();

    private static final Currency TEST_CURRENCY = Currency.RUB;

    @Nested
    @DisplayName("toFullForm tests")
    class ToFullFormTests {

        static Stream<TestCase> data() {
            return Stream.of(
                    new TestCase(new BigDecimal("-1.25"), TEST_CURRENCY, "-1 (один) рубль 25 (двадцать пять) копеек", false), // expected substring, so 'false' for contains
                    new TestCase(new BigDecimal("2.02"), TEST_CURRENCY, "2 (два) рубля 02 (две) копейки", false),
                    new TestCase(new BigDecimal("-5.11"), TEST_CURRENCY, "-5 (пять) рублей 11 (одиннадцать) копеек", false)
            );
        }

        @ParameterizedTest(name = "{index} amount={0}")
        @MethodSource("data")
        void testToFullForm(TestCase testCase) {
            String result = formatter.toFullForm(testCase.amount, testCase.currency);
            assertEquals(testCase.expected, result);
            assertTrue(result.contains(testCase.expected), "Expected substring not found in: " + result);
        }
    }

    @Nested
    @DisplayName("toStandardForm tests")
    class ToStandardFormTests {

        static Stream<TestCase> data() {
            return Stream.of(
                    new TestCase(new BigDecimal("1.01"), TEST_CURRENCY, "1 рубль 01 копейка", true),
                    new TestCase(new BigDecimal("2.02"), TEST_CURRENCY, "2 рубля 02 копейки", true),
                    new TestCase(new BigDecimal("-5.11"), TEST_CURRENCY, "-5 рублей 11 копеек", true)
            );
        }

        @ParameterizedTest(name = "{index} amount={0}")
        @MethodSource("data")
        void testToStandardForm(TestCase testCase) {
            String result = formatter.toStandardForm(testCase.amount, testCase.currency);
            assertEquals(testCase.expected, result);
        }
    }

    @Nested
    @DisplayName("toShortForm tests")
    class ToShortFormTests {

        static Stream<TestCase> data() {
            return Stream.of(
                    new TestCase(new BigDecimal("100.25"), TEST_CURRENCY, "100 руб. 25 коп.", true),
                    new TestCase(new BigDecimal("0.99"), TEST_CURRENCY, "0 руб. 99 коп.", true),
                    new TestCase(new BigDecimal("-1.00"), TEST_CURRENCY, "-1 руб. 00 коп.", true)
            );
        }

        @ParameterizedTest(name = "{index} amount={0}")
        @MethodSource("data")
        void testToShortForm(TestCase testCase) {
            String result = formatter.toShortForm(testCase.amount, testCase.currency);
            assertEquals(testCase.expected, result);
        }
    }

    private record TestCase(BigDecimal amount, Currency currency, String expected, boolean exactMatch) {
    }
}