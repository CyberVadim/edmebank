package ru.edmebank.clients.app.api.dev.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.edmebank.contracts.dto.GenerateDtoRequest;

@RequestMapping("/api/generate/dto")
public interface GeneratedDtoApi {

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Object> generatedDto(@RequestBody GenerateDtoRequest generateDtoRequest);
}
