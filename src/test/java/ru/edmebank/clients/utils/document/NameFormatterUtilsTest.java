package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.github.petrovich4j.Case.Genitive;
import static com.github.petrovich4j.Gender.Male;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.edmebank.clients.utils.document.NameFormatterUtils.declineFullName;
import static ru.edmebank.clients.utils.document.NameFormatterUtils.fullName;
import static ru.edmebank.clients.utils.document.NameFormatterUtils.shortName;

class NameFormatterUtilsTest {
    @Nested
    class FullNameTests {
        @Test
        void returnsConcatenatedFullName() {
            assertEquals("Иванов Иван Иванович",
                    fullName("Иванов", "Иван", "Иванович"));
        }

        @Test
        void handlesEmptyStringsGracefully() {
            assertEquals("", fullName("", "", ""));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("", fullName(null, null, null));
            assertEquals("Иванов Иван", fullName("Иванов", "Иван", null));
        }
    }

    @Nested
    class ShortNameTests {
        @Test
        void withMiddleName() {
            assertEquals("Петров П.С.",
                    shortName("Петров", "Пётр", "Сергеевич"));
        }

        @Test
        void withoutMiddleName() {
            assertEquals("Петров П.",
                    shortName("Петров", "Пётр", ""));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("Петров П.", shortName("Петров", "Пётр", null));
            assertEquals("", shortName(null, null, null));
        }
    }

    @Nested
    class DeclineFullNameTests {
        @ParameterizedTest(name = "[{index}] {0} {1} {2} ({3}, {4}) → {5}")
        @CsvSource({
                "Иванов, Иван, Иванович, Male, Genitive, Иванова Ивана Ивановича",
                "Петрова, Анна, Сергеевна, Female, Dative, Петровой Анне Сергеевне",
                "Тимченко, Антон, Сергеевич, Male, Genitive, Тимченко Антона Сергеевича",
                "Тимченко, Антон, Сергеевич, Male, Dative, Тимченко Антону Сергеевичу",
                "Тимченко, Антон, Сергеевич, Male, Instrumental, Тимченко Антоном Сергеевичем",
                "Тимченко, Антон, Сергеевич, Male, Prepositional, Тимченко Антоне Сергеевиче"
        })
        void declinesCorrectly(String last, String first, String middle,
                               String gender, String grammaticalCase, String expected) {
            Gender valueGender = Gender.valueOf(gender);
            Case valueCase = Case.valueOf(grammaticalCase);

            assertEquals(expected,
                    declineFullName(last, first, middle, valueGender, valueCase));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("", declineFullName(null, null, null, null, null));
            assertEquals("", declineFullName(null, null, "Сергеевич", Male, Genitive));
            assertEquals("Иванова Ивана", declineFullName("Иванов", "Иван", null, Male, Genitive));
        }
    }
}