package ru.edmebank.clients.fw.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import ru.edmebank.print.app.exception.FontLoadException;
import ru.edmebank.print.app.exception.FontNotFoundException;
import ru.edmebank.print.app.exception.HtmlGenerationException;
import ru.edmebank.print.app.exception.PdfGenerationException;

import java.util.Map;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found", ex);
        return buildResponse("ENTITY_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid input", ex);
        return buildResponse("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getMostSpecificCause();
        log.warn("Ошибка парсинга JSON", rootCause);
        return buildResponse("INVALID_JSON", rootCause.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ErrorResponse> handlePdfGeneration(PdfGenerationException e) {
        log.error("Ошибка генерации PDF", e);
        return buildResponse("PDF_GENERATION_FAILED", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FontNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFontNotFound(FontNotFoundException e) {
        log.error("Шрифт не найден", e);
        return buildResponse("FONT_NOT_FOUND", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FontLoadException.class)
    public ResponseEntity<ErrorResponse> handleFontLoad(FontLoadException e) {
        log.error("Ошибка загрузки шрифта", e);
        return buildResponse("FONT_LOAD_FAILED", "Произошла внутренняя ошибка при генерации документа. Попробуйте позже.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HtmlGenerationException.class)
    public ResponseEntity<ErrorResponse> handleHtmlGeneration(HtmlGenerationException e) {
        log.error("Ошибка генерации HTML из шаблона", e);
        return buildResponse("HTML_GENERATION_FAILED", "Произошла ошибка при формировании документа. Попробуйте позже.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String code, String message, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(code, message, Map.of());
        return ResponseEntity.status(status).body(error);
    }
}
