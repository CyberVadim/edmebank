package ru.edmebank.clients.app.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.edmebank.contracts.dto.ClientUiDto;

@RequestMapping("/api/ui/client")
public interface UiClientApi {

    @RequestMapping(method = RequestMethod.POST,
    consumes = {"application/json"},
    produces = {"application/json", "application/pdf"})
    default ResponseEntity<byte[]> completeCreateClient(@RequestBody @Valid ClientUiDto clientUiDto) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
