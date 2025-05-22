package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameFormatterUtilsTest {

    @Test
    void testFullName() {
        String lastName = "Иванов";
        String firstName = "Иван";
        String middleName = "Иванович";

        String expected = "Иванов Иван Иванович";
        String actual = NameFormatterUtils.fullName(lastName, firstName, middleName);

        assertEquals(expected, actual);
    }

    @Test
    void testShortNameWithMiddleName() {
        String lastName = "Петров";
        String firstName = "Пётр";
        String middleName = "Сергеевич";

        String expected = "Петров П. С.";
        String actual = NameFormatterUtils.shortName(lastName, firstName, middleName);

        assertEquals(expected, actual);
    }

    @Test
    void testShortNameWithoutMiddleName() {
        String lastName = "Петров";
        String firstName = "Пётр";
        String middleName = "";

        String expected = "Петров П.";
        String actual = NameFormatterUtils.shortName(lastName, firstName, middleName);

        assertEquals(expected, actual);
    }

    @Test
    void testDeclineFullNameGenitiveMale() {
        String lastName = "Иванов";
        String firstName = "Иван";
        String middleName = "Иванович";

        String expected = "Иванова Ивана Ивановича";
        String actual = NameFormatterUtils.declineFullName(
                lastName,
                firstName,
                middleName,
                Gender.Male,
                Case.Genitive);

        assertEquals(expected, actual);
    }

    @Test
    void testDeclineFullNameDativeFemale() {
        String lastName = "Петрова";
        String firstName = "Анна";
        String middleName = "Сергеевна";

        String expected = "Петровой Анне Сергеевне";
        String actual = NameFormatterUtils.declineFullName(
                lastName,
                firstName,
                middleName,
                Gender.Female,
                Case.Dative);

        assertEquals(expected, actual);
    }

    @Test
    void testDeclineFullNameGenitive() {
        String lastName = "Тимченко";
        String firstName = "Антон";
        String middleName = "Сергеевич";
        Gender gender = Gender.Male;
        Case nameCase = Case.Genitive;

        String result = NameFormatterUtils.declineFullName(lastName, firstName, middleName, gender, nameCase);
        assertEquals("Тимченко Антона Сергеевича", result);
    }

    @Test
    void testDeclineFullNameDative() {
        String lastName = "Тимченко";
        String firstName = "Антон";
        String middleName = "Сергеевич";
        Gender gender = Gender.Male;
        Case nameCase = Case.Dative;

        String result = NameFormatterUtils.declineFullName(lastName, firstName, middleName, gender, nameCase);
        assertEquals("Тимченко Антону Сергеевичу"   , result);
    }

    @Test
    void testDeclineFullNameInstrumental() {
        String lastName = "Тимченко";
        String firstName = "Антон";
        String middleName = "Сергеевич";
        Gender gender = Gender.Male;
        Case nameCase = Case.Instrumental;

        String result = NameFormatterUtils.declineFullName(lastName, firstName, middleName, gender, nameCase);

        assertEquals("Тимченко Антоном Сергеевичем", result);
    }

    @Test
    void testDeclineFullNamePrepositional() {
        String lastName = "Тимченко";
        String firstName = "Антон";
        String middleName = "Сергеевич";
        Gender gender = Gender.Male;
        Case nameCase = Case.Prepositional;

        String result = NameFormatterUtils.declineFullName(lastName, firstName, middleName, gender, nameCase);

        assertEquals("Тимченко Антоне Сергеевиче", result);
    }
}