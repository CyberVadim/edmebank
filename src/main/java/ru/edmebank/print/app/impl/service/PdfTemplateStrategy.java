package ru.edmebank.print.app.impl.service;


import ru.edmebank.contracts.dto.print.PrintRequestDto;

public interface PdfTemplateStrategy {
    String generateHtml(PrintRequestDto request);
}

