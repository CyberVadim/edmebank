package ru.edmebank.clients.fw.exception;

public class DtoGenerationException extends RuntimeException {

    private static final String INFO = "Ошибка генерации, ";

    public DtoGenerationException(String message) {
        super(INFO + message);
    }
}
