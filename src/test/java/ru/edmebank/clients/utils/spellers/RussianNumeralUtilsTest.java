package ru.edmebank.clients.utils.spellers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.petrovich4j.Gender.Female;
import static com.github.petrovich4j.Gender.Male;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.edmebank.clients.utils.spellers.RussianNumeralUtils.correctGender;
import static ru.edmebank.clients.utils.spellers.RussianNumeralUtils.declension;

class RussianNumeralUtilsTest {

    @Nested
    @DisplayName("declension() tests")
    class DeclensionTests {
        static Stream<Arguments> declensionData() {
            String[] forms = {"рубля", "рублей"};
            String formZero = "рубль";

            return of(
                    arg(0, formZero, forms, "рублей"),
                    arg(5, formZero, forms, "рублей"),
                    arg(11, formZero, forms, "рублей"),
                    arg(19, formZero, forms, "рублей"),
                    arg(100, formZero, forms, "рублей"),

                    arg(1, formZero, forms, "рубль"),
                    arg(21, formZero, forms, "рубль"),
                    arg(101, formZero, forms, "рубль"),

                    arg(2, formZero, forms, "рубля"),
                    arg(3, formZero, forms, "рубля"),
                    arg(4, formZero, forms, "рубля"),
                    arg(22, formZero, forms, "рубля"),

                    arg(-1, formZero, forms, "рубль"),
                    arg(-2, formZero, forms, "рубля"),
                    arg(-11, formZero, forms, "рублей")
            );
        }

        @ParameterizedTest(name = "declension({0}) → {3}")
        @MethodSource("declensionData")
        void testDeclension(int number, String formZero, String[] forms, String expected) {
            String result = declension(number, formZero, forms);
            assertEquals(expected, result);
        }

        private static Arguments arg(int n, String f0, String[] f, String expected) {
            return Arguments.of(n, f0, f, expected);
        }
    }

    @Nested
    @DisplayName("correctGender() tests")
    class CorrectGenderTests {
        @Test
        void testMaleGenderUnchanged() {
            assertEquals("один", correctGender("один", Male));
            assertEquals("два", correctGender("два", Male));
            assertEquals("три", correctGender("три", Male));
        }

        @Test
        void testFemaleGenderConversion() {
            assertEquals("одна", correctGender("один", Female));
            assertEquals("две", correctGender("два", Female));
        }

        @Test
        void testFemaleGenderDoesNotAffectOthers() {
            assertEquals("три", correctGender("три", Female));
        }

        @Test
        void testExactWordBoundaryReplacement() {
            assertEquals("только одна", correctGender("только один", Female));
            assertEquals("всего две", correctGender("всего два", Female));
        }

        @Test
        void testPartialMatchShouldNotReplace() {
            assertEquals("одинокий", correctGender("одинокий", Female));
            assertEquals("дважды", correctGender("дважды", Female));
        }
    }
}