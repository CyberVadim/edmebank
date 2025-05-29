package ru.edmebank.clients.utils.spellers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.edmebank.contracts.enums.DeclensionType;

import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formAccusativeDeclension;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formDativeDeclension;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formGenitiveDeclension;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formInstrumentalDeclension;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formNominativeDeclension;
import static ru.edmebank.clients.utils.spellers.NumeralDeclensionUtils.formPrepositionalDeclension;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_ACCUSATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_DATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_GENITIVE;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_INSTRUMENTAL;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_NOMINATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.PLURAL_PREPOSITIONAL;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_ACCUSATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_DATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_GENITIVE;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_INSTRUMENTAL;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_NOMINATIVE;
import static ru.edmebank.contracts.enums.DeclensionType.SINGULAR_PREPOSITIONAL;

class NumeralDeclensionUtilsTest {
    @Nested
    class NominativeDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_NOMINATIVE),
                    Arguments.of(2, PLURAL_NOMINATIVE),
                    Arguments.of(5, PLURAL_GENITIVE),
                    Arguments.of(11, PLURAL_GENITIVE),
                    Arguments.of(21, SINGULAR_NOMINATIVE),
                    Arguments.of(30, PLURAL_GENITIVE),
                    Arguments.of(31, SINGULAR_NOMINATIVE),
                    Arguments.of(101, SINGULAR_NOMINATIVE),
                    Arguments.of(365, PLURAL_GENITIVE)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormNominativeDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formNominativeDeclension(number);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class GenitiveDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_GENITIVE),
                    Arguments.of(2, PLURAL_GENITIVE),
                    Arguments.of(5, PLURAL_GENITIVE),
                    Arguments.of(11, PLURAL_GENITIVE),
                    Arguments.of(21, SINGULAR_GENITIVE),
                    Arguments.of(30, PLURAL_GENITIVE),
                    Arguments.of(31, SINGULAR_GENITIVE),
                    Arguments.of(101, SINGULAR_GENITIVE),
                    Arguments.of(365, PLURAL_GENITIVE)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormGenitiveDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formGenitiveDeclension(number);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class DativeDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_DATIVE),
                    Arguments.of(2, PLURAL_DATIVE),
                    Arguments.of(5, PLURAL_DATIVE),
                    Arguments.of(11, PLURAL_DATIVE),
                    Arguments.of(21, SINGULAR_DATIVE),
                    Arguments.of(30, PLURAL_DATIVE),
                    Arguments.of(31, SINGULAR_DATIVE),
                    Arguments.of(101, SINGULAR_DATIVE),
                    Arguments.of(365, PLURAL_DATIVE)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormDativeDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formDativeDeclension(number);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class AccusativeDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_ACCUSATIVE),
                    Arguments.of(2, PLURAL_ACCUSATIVE),
                    Arguments.of(5, PLURAL_ACCUSATIVE),
                    Arguments.of(11, PLURAL_ACCUSATIVE),
                    Arguments.of(21, SINGULAR_ACCUSATIVE),
                    Arguments.of(30, PLURAL_ACCUSATIVE),
                    Arguments.of(31, SINGULAR_ACCUSATIVE),
                    Arguments.of(101, SINGULAR_ACCUSATIVE),
                    Arguments.of(365, PLURAL_ACCUSATIVE)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormAccusativeDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formAccusativeDeclension(number);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class InstrumentalDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_INSTRUMENTAL),
                    Arguments.of(2, PLURAL_INSTRUMENTAL),
                    Arguments.of(5, PLURAL_INSTRUMENTAL),
                    Arguments.of(11, PLURAL_INSTRUMENTAL),
                    Arguments.of(21, SINGULAR_INSTRUMENTAL),
                    Arguments.of(30, PLURAL_INSTRUMENTAL),
                    Arguments.of(31, SINGULAR_INSTRUMENTAL),
                    Arguments.of(101, SINGULAR_INSTRUMENTAL),
                    Arguments.of(365, PLURAL_INSTRUMENTAL)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormInstrumentalDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formInstrumentalDeclension(number);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class PrepositionalDeclensionTests {
        static Stream<Arguments> provideNumbersForDeclension() {
            return of(
                    Arguments.of(1, SINGULAR_PREPOSITIONAL),
                    Arguments.of(2, PLURAL_PREPOSITIONAL),
                    Arguments.of(5, PLURAL_PREPOSITIONAL),
                    Arguments.of(11, PLURAL_PREPOSITIONAL),
                    Arguments.of(21, SINGULAR_PREPOSITIONAL),
                    Arguments.of(30, PLURAL_PREPOSITIONAL),
                    Arguments.of(31, SINGULAR_PREPOSITIONAL),
                    Arguments.of(101, SINGULAR_PREPOSITIONAL),
                    Arguments.of(365, PLURAL_PREPOSITIONAL)
            );
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForDeclension")
        void testFormPrepositionalDeclension(int number, DeclensionType expectedDeclension) {
            String expected = expectedDeclension.getFormattedString(number);
            String actual = formPrepositionalDeclension(number);
            assertEquals(expected, actual);
        }
    }
}