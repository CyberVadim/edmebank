package ru.edmebank.print.app.impl.service;

import org.springframework.stereotype.Component;
import ru.edmebank.contracts.enums.TemplateType;

import java.util.Map;

@Component
public class StrategySelector {

    private final Map<String, PdfTemplateStrategy> strategies;

    public StrategySelector(Map<String, PdfTemplateStrategy> strategies) {
        this.strategies = strategies;
    }

    public PdfTemplateStrategy getStrategy(TemplateType type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип шаблона не передан");
        }

        String key = type.name();
        if (!strategies.containsKey(key)) {
            throw new IllegalArgumentException("Неподдерживаемый тип шаблона: " + type);
        }
        return strategies.get(key);
    }
}
