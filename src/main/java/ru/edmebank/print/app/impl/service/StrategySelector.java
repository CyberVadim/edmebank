package ru.edmebank.print.app.impl.service;

import org.springframework.stereotype.Component;
import ru.edmebank.contracts.enums.TemplateType;

import java.util.EnumMap;
import java.util.Map;

@Component
public class StrategySelector {

    private final EnumMap<TemplateType, PdfTemplateStrategy> strategies;

    public StrategySelector(Map<String, PdfTemplateStrategy> beans) {
        this.strategies = new EnumMap<>(TemplateType.class);

        for (Map.Entry<String, PdfTemplateStrategy> entry : beans.entrySet()) {
            TemplateType type = TemplateType.valueOf(entry.getKey());
            strategies.put(type, entry.getValue());
        }
    }

    public PdfTemplateStrategy getStrategy(TemplateType type) {
        PdfTemplateStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Неподдерживаемый тип шаблона: " + type);
        }
        return strategy;
    }
}
