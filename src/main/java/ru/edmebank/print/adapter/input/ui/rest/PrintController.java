package ru.edmebank.print.adapter.input.ui.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.edmebank.contracts.dto.print.PrintRequestDto;
import ru.edmebank.print.app.api.PrintApi;
import ru.edmebank.print.app.impl.service.PdfGenerationService;

@RestController
@RequiredArgsConstructor
public class PrintController implements PrintApi {

    private final PdfGenerationService pdfService;

    @Override
    public ResponseEntity<byte[]> generatePdf(PrintRequestDto request) {
        byte[] pdf = pdfService.generatePdf(request);
        String fileName = request.type().toString().toLowerCase() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }
}
