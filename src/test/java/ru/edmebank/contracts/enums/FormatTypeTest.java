package ru.edmebank.contracts.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.edmebank.clients.utils.currency.AmountParts;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatTypeTest {

    private static final Currency TEST_CURRENCY = Currency.RUB;

    @Nested
    @DisplayName("FULL format")
    class FullFormatTests {

        static Stream<TestCase> fullCases() {
            return Stream.of(
                    new TestCase(new AmountParts("-", 1, 25), "-1 (один) рубль 25 (двадцать пять) копеек"),
                    new TestCase(new AmountParts("", 2, 2), "2 (два) рубля 02 (две) копейки"),
                    new TestCase(new AmountParts("+", 5, 11), "+5 (пять) рублей 11 (одиннадцать) копеек")
            );
        }

        @ParameterizedTest(name = "{index}: parts={0} → expected contains \"{1}\"")
        @MethodSource("fullCases")
        void testFullFormat(TestCase testCase) {
            String actual = FormatType.FULL.format(testCase.parts, TEST_CURRENCY);
            assertTrue(actual.contains(testCase.expectedSnippet),
                    () -> "Ожидали подстроку: " + testCase.expectedSnippet + ", но было: " + actual);
        }
    }

    @Nested
    @DisplayName("STANDARD format")
    class StandardFormatTests {

        static Stream<TestCase> standardCases() {
            return Stream.of(
                    new TestCase(new AmountParts("", 1, 1), "1 рубль 01 копейка"),
                    new TestCase(new AmountParts("", 2, 2), "2 рубля 02 копейки"),
                    new TestCase(new AmountParts("-", 5, 11), "-5 рублей 11 копеек")
            );
        }

        @ParameterizedTest(name = "{index}: parts={0} → expected: \"{1}\"")
        @MethodSource("standardCases")
        void testStandardFormat(TestCase testCase) {
            String actual = FormatType.STANDARD.format(testCase.parts, TEST_CURRENCY);
            assertEquals(testCase.expectedSnippet, actual);
        }
    }

    @Nested
    @DisplayName("SHORT format")
    class ShortFormatTests {

        static Stream<TestCase> shortCases() {
            return Stream.of(
                    new TestCase(new AmountParts("+", 100, 25), "+100 руб. 25 коп."),
                    new TestCase(new AmountParts("", 0, 99), "0 руб. 99 коп."),
                    new TestCase(new AmountParts("-", 1, 0), "-1 руб. 00 коп.")
            );
        }

        @ParameterizedTest(name = "{index}: parts={0} → expected: \"{1}\"")
        @MethodSource("shortCases")
        void testShortFormat(TestCase testCase) {
            String actual = FormatType.SHORT.format(testCase.parts, TEST_CURRENCY);
            assertEquals(testCase.expectedSnippet, actual);
        }
    }

    record TestCase(AmountParts parts, String expectedSnippet) {
        @Override
        public String toString() {
            return parts.toString();
        }
    }
}