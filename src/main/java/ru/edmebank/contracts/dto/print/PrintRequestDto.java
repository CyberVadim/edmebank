package ru.edmebank.contracts.dto.print;

import ru.edmebank.contracts.enums.TemplateType;

public record PrintRequestDto(
        TemplateType type,
        String stringContent
) {}