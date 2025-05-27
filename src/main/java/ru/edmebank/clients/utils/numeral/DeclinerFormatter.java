package ru.edmebank.clients.utils.numeral;

import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formAccusativeDeclension;
import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formDativeDeclension;
import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formGenitiveDeclension;
import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formInstrumentalDeclension;
import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formNominativeDeclension;
import static ru.edmebank.clients.utils.numeral.NumeralDeclensionUtils.formPrepositionalDeclension;

public interface DeclinerFormatter {
    String getNominativeDeclension(int number);

    String getGenitiveDeclension(int number);

    String getDativeDeclension(int number);

    String getAccusativeDeclension(int number);

    String getInstrumentalDeclension(int number);

    String getPrepositionalDeclension(int number);

    default String formNominative(int number) {
        return formNominativeDeclension(number);
    }

    default String formGenitive(int number) {
        return formGenitiveDeclension(number);
    }

    default String formDative(int number) {
        return formDativeDeclension(number);
    }

    default String formAccusative(int number) {
        return formAccusativeDeclension(number);
    }

    default String formInstrumental(int number) {
        return formInstrumentalDeclension(number);
    }

    default String formPrepositional(int number) {
        return formPrepositionalDeclension(number);
    }
}
