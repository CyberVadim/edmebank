package ru.edmebank.print.app.exception;

public class HtmlGenerationException extends RuntimeException {
    public HtmlGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
