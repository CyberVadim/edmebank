package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameFormatterUtilsTest {

    @Nested
    class FullNameTests {
        @Test
        void returnsConcatenatedFullName() {
            assertEquals("Иванов Иван Иванович",
                    NameFormatterUtils.fullName("Иванов", "Иван", "Иванович"));
        }

        @Test
        void handlesEmptyStringsGracefully() {
            assertEquals("  ", NameFormatterUtils.fullName("", "", ""));
        }
    }

    @Nested
    class ShortNameTests {
        @Test
        void withMiddleName() {
            assertEquals("Петров П. С.",
                    NameFormatterUtils.shortName("Петров", "Пётр", "Сергеевич"));
        }

        @Test
        void withoutMiddleName() {
            assertEquals("Петров П.",
                    NameFormatterUtils.shortName("Петров", "Пётр", ""));
        }
    }

    @Nested
    class DeclineFullNameTests {
        @ParameterizedTest(name = "[{index}] {0} {1} {2} ({3}, {4}) → {5}")
        @MethodSource("validCases")
        void declinesCorrectly(String last, String first, String middle,
                               Gender gender, Case grammaticalCase, String expected) {
            assertEquals(expected,
                    NameFormatterUtils.declineFullName(last, first, middle, gender, grammaticalCase));
        }

        static Stream<org.junit.jupiter.params.provider.Arguments> validCases() {
            return Stream.of(
                    arg("Иванов", "Иван", "Иванович", Gender.Male, Case.Genitive, "Иванова Ивана Ивановича"),
                    arg("Петрова", "Анна", "Сергеевна", Gender.Female, Case.Dative, "Петровой Анне Сергеевне"),
                    arg("Тимченко", "Антон", "Сергеевич", Gender.Male, Case.Genitive, "Тимченко Антона Сергеевича"),
                    arg("Тимченко", "Антон", "Сергеевич", Gender.Male, Case.Dative, "Тимченко Антону Сергеевичу"),
                    arg("Тимченко", "Антон", "Сергеевич", Gender.Male, Case.Instrumental, "Тимченко Антоном Сергеевичем"),
                    arg("Тимченко", "Антон", "Сергеевич", Gender.Male, Case.Prepositional, "Тимченко Антоне Сергеевиче")
            );
        }

        private static org.junit.jupiter.params.provider.Arguments arg(Object... args) {
            return org.junit.jupiter.params.provider.Arguments.of(args);
        }
    }
}