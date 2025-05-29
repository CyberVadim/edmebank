package ru.edmebank.clients.utils.spellers;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.github.petrovich4j.Case.Genitive;
import static com.github.petrovich4j.Gender.Male;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.edmebank.clients.utils.spellers.NameFormatterUtils.formName;
import static ru.edmebank.clients.utils.spellers.NameFormatterUtils.formDeclineName;

class NameFormatterUtilsTest {
    @Nested
    class FullNameTests {
        @Test
        void returnsConcatenatedFullName() {
            assertEquals("Иванов Иван Иванович",
                    formName("Иванов", "Иван", "Иванович", false));
        }

        @Test
        void handlesEmptyStringsGracefully() {
            assertEquals("", formName("", "", "", false));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("", formName(null, null, null, false));
            assertEquals("Иванов Иван", formName("Иванов", "Иван", null, false));
        }
    }

    @Nested
    class ShortNameTests {
        @Test
        void withMiddleName() {
            assertEquals("Петров П.С.",
                    formName("Петров", "Пётр", "Сергеевич", true));
        }

        @Test
        void withoutMiddleName() {
            assertEquals("Петров П.",
                    formName("Петров", "Пётр", "", true));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("Петров П.", formName("Петров", "Пётр", null, true));
            assertEquals("", formName(null, null, null, true));
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
                    formDeclineName(last, first, middle, valueGender, valueCase));
        }

        @Test
        void handlesNullValuesGracefully() {
            assertEquals("", formDeclineName(null, null, null, null, null));
            assertEquals("", formDeclineName(null, null, "Сергеевич", Male, Genitive));
            assertEquals("Иванова Ивана", formDeclineName("Иванов", "Иван", null, Male, Genitive));
        }
    }
}