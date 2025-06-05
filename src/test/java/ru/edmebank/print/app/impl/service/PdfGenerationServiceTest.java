package ru.edmebank.print.app.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.contracts.enums.TemplateType;
import ru.edmebank.print.app.exception.PdfGenerationException;
import ru.edmebank.print.utils.FontUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfGenerationServiceTest {

    @Mock
    private StrategySelector strategySelector;

    @InjectMocks
    private PdfGenerationService pdfService;

    @Test
    void shouldGeneratePdfSuccessfully() throws Exception {
        // given
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, "{}");
        PdfTemplateStrategy strategy = mock(PdfTemplateStrategy.class);
        when(strategySelector.getStrategy(request.type())).thenReturn(strategy);
        when(strategy.generateHtml(request)).thenReturn("<html></html>");

        try (MockedStatic<FontUtils> mockedFontUtils = mockStatic(FontUtils.class)) {
            mockedFontUtils.when(() -> FontUtils.loadFont(any()))
                    .thenReturn(new File("src/test/resources/fonts/times.ttf"));

            // when
            byte[] pdf = pdfService.generatePdf(request);

            // then
            assertNotNull(pdf);
            assertTrue(pdf.length > 0);
        }
    }

    @Test
    void shouldThrowPdfGenerationExceptionWhenFontMissing() throws Exception {
        // given
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, "{}");
        PdfTemplateStrategy strategy = mock(PdfTemplateStrategy.class);
        when(strategySelector.getStrategy(request.type())).thenReturn(strategy);
        when(strategy.generateHtml(request)).thenReturn("<html></html>");

        try (MockedStatic<FontUtils> mockedFontUtils = mockStatic(FontUtils.class)) {
            mockedFontUtils.when(() -> FontUtils.loadFont(any()))
                    .thenThrow(new RuntimeException("Font error"));

            // when / then
            assertThrows(PdfGenerationException.class, () -> pdfService.generatePdf(request));
        }
    }


}

