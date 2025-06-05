package ru.edmebank.print.app.impl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.edmebank.contracts.dto.print.ConsentDataPrintDto;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.print.app.exception.HtmlGenerationException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component("UNIVERSAL_PERSONAL_DATA")
@RequiredArgsConstructor
@Slf4j
public class UniversalPersonalDataStrategy implements PdfTemplateStrategy {

    private final Configuration freemarkerConfig;
    private final ObjectMapper objectMapper;

    private static final String DEFAULT_FIRST_NAME = "_________";
    private static final String DEFAULT_LAST_NAME = "____________";
    private static final String DEFAULT_MIDDLE_NAME = "_____________";
    private static final String DEFAULT_BIRTH_DATE = "____.__.__";
    private static final String DEFAULT_EMAIL = "_______@____.___";

    private static final Map<String, Object> DEFAULT_MODEL = Map.of(
            "firstName", DEFAULT_FIRST_NAME,
            "lastName", DEFAULT_LAST_NAME,
            "middleName", DEFAULT_MIDDLE_NAME,
            "birthDate", DEFAULT_BIRTH_DATE,
            "email", DEFAULT_EMAIL
    );


    @Override
    public String generateHtml(PrintRequestDto request) {
        try {
            Map<String, Object> model = new HashMap<>(DEFAULT_MODEL);

            if (request.stringContent() != null && !request.stringContent().isBlank()) {
                ConsentDataPrintDto data = objectMapper.readValue(request.stringContent(), ConsentDataPrintDto.class);
                fillModelFromData(model, data);
            }

            Template template = freemarkerConfig.getTemplate("consent.ftlh");
            StringWriter writer = new StringWriter();
            template.process(model, writer);

            return writer.toString();

        } catch (IOException | freemarker.template.TemplateException e) {
            throw new HtmlGenerationException("Ошибка генерации HTML-шаблона", e);
        }
    }

    private void fillModelFromData(Map<String, Object> model, ConsentDataPrintDto data) {
        putIfNotNull(model, "firstName", data.firstName());
        putIfNotNull(model, "lastName", data.lastName());
        putIfNotNull(model, "middleName", data.middleName());
        putIfNotNull(model, "birthDate", data.birthDate());
        putIfNotNull(model, "email", data.email());
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
