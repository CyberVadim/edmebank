package ru.edmebank.print.app.impl.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.contracts.enums.TemplateType;
import ru.edmebank.print.app.exception.PdfGenerationException;
import ru.edmebank.print.utils.FontUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private static final String FONT_PATH = "fonts/times.ttf";
    private static final String FONT_FAMILY_NAME = "Times New Roman";
    private static final String BASE_URI = "file:///";
    private static final String PDF_ERROR_MESSAGE = "Ошибка при генерации PDF";

    private final StrategySelector strategySelector;

    public byte[] generatePdf(PrintRequestDto request) {
        String html = generateHtml(request);
        return renderPdf(html, request.type());
    }

    private String generateHtml(PrintRequestDto request) {
        return strategySelector.getStrategy(request.type()).generateHtml(request);
    }

    private byte[] renderPdf(String html, TemplateType type) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = createBuilder(html, os);
            builder.run();

            log.info("PDF успешно сгенерирован для шаблона {}", type);
            return os.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException(PDF_ERROR_MESSAGE, e);
        }
    }

    private PdfRendererBuilder createBuilder(String html, ByteArrayOutputStream os) {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.toStream(os);
        builder.withHtmlContent(html, BASE_URI);

        File fontFile = FontUtils.loadFont(FONT_PATH);
        builder.useFont(fontFile, FONT_FAMILY_NAME);

        return builder;
    }
}
