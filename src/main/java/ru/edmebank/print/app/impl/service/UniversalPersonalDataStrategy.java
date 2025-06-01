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

    private static final Map<String, Object> DEFAULT_MODEL = Map.of(
            "firstName", "_________",
            "lastName", "____________",
            "middleName", "_____________",
            "birthDate", "____.__.__",
            "email", "_______@____.___"
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
        if (data.firstName() != null) model.put("firstName", data.firstName());
        if (data.lastName() != null) model.put("lastName", data.lastName());
        if (data.middleName() != null) model.put("middleName", data.middleName());
        if (data.birthDate() != null) model.put("birthDate", data.birthDate());
        if (data.email() != null) model.put("email", data.email());
    }
}
