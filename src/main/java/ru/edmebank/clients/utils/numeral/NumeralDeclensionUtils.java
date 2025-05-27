package ru.edmebank.clients.utils.numeral;

import lombok.experimental.UtilityClass;
import ru.edmebank.contracts.enums.DeclensionType;

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

@UtilityClass
public class NumeralDeclensionUtils {
    public static String formNominativeDeclension(int number) {
        return getNominative(number).getFormattedString(number);
    }

    public static String formGenitiveDeclension(int number) {
        return getGenitive(number).getFormattedString(number);
    }

    public static String formDativeDeclension(int number) {
        return getDative(number).getFormattedString(number);
    }

    public static String formAccusativeDeclension(int number) {
        return getAccusative(number).getFormattedString(number);
    }

    public static String formInstrumentalDeclension(int number) {
        return getInstrumental(number).getFormattedString(number);
    }

    public static String formPrepositionalDeclension(int number) {
        return getPrepositional(number).getFormattedString(number);
    }

    private static DeclensionType getNominative(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_NOMINATIVE :
                (number % 10 >= 2 && number % 10 <= 4 && !(number % 100 >= 12 && number % 100 <= 14)) ?
                        PLURAL_NOMINATIVE :
                        PLURAL_GENITIVE;
    }

    private static DeclensionType getGenitive(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_GENITIVE :
                PLURAL_GENITIVE;
    }

    private static DeclensionType getDative(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_DATIVE :
                PLURAL_DATIVE;
    }

    private static DeclensionType getAccusative(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_ACCUSATIVE :
                PLURAL_ACCUSATIVE;
    }

    private static DeclensionType getInstrumental(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_INSTRUMENTAL :
                PLURAL_INSTRUMENTAL;
    }

    private static DeclensionType getPrepositional(int number) {
        return (number % 10 == 1 && number % 100 != 11) ?
                SINGULAR_PREPOSITIONAL :
                PLURAL_PREPOSITIONAL;
    }
}
