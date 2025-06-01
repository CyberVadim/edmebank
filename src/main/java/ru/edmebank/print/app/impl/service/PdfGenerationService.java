package ru.edmebank.print.app.impl.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.print.app.exception.PdfGenerationException;
import ru.edmebank.print.utils.FontUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final StrategySelector strategySelector;

    public byte[] generatePdf(PrintRequestDto request) {
        String html = strategySelector.getStrategy(request.type()).generateHtml(request);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.toStream(os);
            builder.withHtmlContent(html, "file:///");

            File fontFile = FontUtils.loadFont("fonts/times.ttf");

            builder.useFont(fontFile, "Times New Roman");
            builder.run();

            log.info("PDF успешно сгенерирован для шаблона {}", request.type());

            return os.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Ошибка при генерации PDF", e);
        }
    }
}
