package ru.edmebank.clients.fw.spellers;

public class DeclinerFormatterImpl implements DeclinerFormatter {
    @Override
    public String getNominativeDeclension(int number) {
        return formNominative(number);
    }

    @Override
    public String getGenitiveDeclension(int number) {
        return formGenitive(number);
    }

    @Override
    public String getDativeDeclension(int number) {
        return formDative(number);
    }

    @Override
    public String getAccusativeDeclension(int number) {
        return formAccusative(number);
    }

    @Override
    public String getInstrumentalDeclension(int number) {
        return formInstrumental(number);
    }

    @Override
    public String getPrepositionalDeclension(int number) {
        return formPrepositional(number);
    }
}
