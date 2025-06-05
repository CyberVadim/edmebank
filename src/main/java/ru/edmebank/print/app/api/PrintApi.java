package ru.edmebank.print.app.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.edmebank.contracts.dto.print.PrintRequestDto;

@RequestMapping("/api/print")
public interface PrintApi {

    @PostMapping(
            value = "/pdf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    default ResponseEntity<byte[]> generatePdf(@RequestBody PrintRequestDto request) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED); // реализация будет в контроллере
    }
}

