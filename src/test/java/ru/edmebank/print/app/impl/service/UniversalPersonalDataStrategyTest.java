package ru.edmebank.print.app.impl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.contracts.enums.TemplateType;
import ru.edmebank.print.app.exception.HtmlGenerationException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UniversalPersonalDataStrategyTest {

    private Configuration freemarkerConfig;
    private Template mockTemplate;
    private UniversalPersonalDataStrategy strategy;

    @BeforeEach
    void setUp() {
        freemarkerConfig = mock(Configuration.class);
        ObjectMapper objectMapper = new ObjectMapper();
        mockTemplate = mock(Template.class);

        strategy = new UniversalPersonalDataStrategy(freemarkerConfig, objectMapper);
    }

    @Test
    void shouldGenerateHtmlFromValidData() throws Exception {
        // given
        String json = """
                    {
                      "firstName": "Иван",
                      "lastName": "Иванов",
                      "middleName": "Иванович",
                      "birthDate": "1990-01-01",
                      "email": "ivan@example.com"
                    }
                """;
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, json);

        when(freemarkerConfig.getTemplate("consent.ftlh")).thenReturn(mockTemplate);
        doAnswer(invocation -> {
            Map<String, Object> model = invocation.getArgument(0);
            Writer writer = invocation.getArgument(1);
            writer.write("<html>" + model.get("firstName") + "</html>");
            return null;
        }).when(mockTemplate).process(any(), any());

        // when
        String result = strategy.generateHtml(request);

        // then
        assertTrue(result.contains("Иван"));
    }

    @Test
    void shouldGenerateHtmlWithDefaultValuesWhenStringContentIsEmpty() throws Exception {
        // given
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, "");

        when(freemarkerConfig.getTemplate("consent.ftlh")).thenReturn(mockTemplate);
        doAnswer(invocation -> {
            Map<String, Object> model = invocation.getArgument(0);
            Writer writer = invocation.getArgument(1);
            writer.write("<html>" + model.get("firstName") + "</html>");
            return null;
        }).when(mockTemplate).process(any(), any());

        // when
        String result = strategy.generateHtml(request);

        // then
        assertTrue(result.contains("_________")); // дефолтное значение
    }

    @Test
    void shouldThrowHtmlGenerationExceptionWhenJsonIsInvalid() {
        // given
        String brokenJson = "{ invalid json }";
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, brokenJson);

        // when / then
        HtmlGenerationException exception = assertThrows(HtmlGenerationException.class, () -> strategy.generateHtml(request));

        assertTrue(exception.getMessage().contains("Ошибка генерации HTML-шаблона"));
    }

    @Test
    void shouldThrowHtmlGenerationExceptionWhenTemplateFails() throws Exception {
        // given
        String json = "{}";
        PrintRequestDto request = new PrintRequestDto(TemplateType.UNIVERSAL_PERSONAL_DATA, json);


        when(freemarkerConfig.getTemplate("consent.ftlh")).thenThrow(new IOException("Template not found"));

        // when / then
        HtmlGenerationException exception = assertThrows(HtmlGenerationException.class, () -> strategy.generateHtml(request));

        assertTrue(exception.getMessage().contains("Ошибка генерации HTML-шаблона"));
    }

}
