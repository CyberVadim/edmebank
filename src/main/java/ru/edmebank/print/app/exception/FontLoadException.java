package ru.edmebank.print.app.exception;

public class FontLoadException extends RuntimeException {
    public FontLoadException(String message) {
        super(message);
    }

    public FontLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

