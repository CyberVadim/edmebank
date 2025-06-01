package ru.edmebank.clients.fw.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.edmebank.print.app.exception.FontLoadException;
import ru.edmebank.print.app.exception.FontNotFoundException;
import ru.edmebank.print.app.exception.HtmlGenerationException;
import ru.edmebank.print.app.exception.PdfGenerationException;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<String> handlePdfErrors(PdfGenerationException e) {
        log.error("Ошибка генерации PDF", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка генерации PDF: " + e.getMessage());
    }


    @ExceptionHandler(FontNotFoundException.class)
    public ResponseEntity<String> handleFontNotFound(FontNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка инфраструктуры: " + e.getMessage());
    }

    @ExceptionHandler(FontLoadException.class)
    public ResponseEntity<String> handleFontLoad(FontLoadException e) {
        log.error("Ошибка загрузки шрифта", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка при генерации документа. Попробуйте позже.");
    }

    @ExceptionHandler(HtmlGenerationException.class)
    public ResponseEntity<String> handleHtmlGenerationError(HtmlGenerationException e) {
        log.error("Ошибка генерации HTML из шаблона Freemarker", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла ошибка при формировании документа. Попробуйте позже.");
    }
}


