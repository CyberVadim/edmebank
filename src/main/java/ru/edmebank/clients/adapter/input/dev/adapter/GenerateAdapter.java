package ru.edmebank.clients.adapter.input.dev.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.edmebank.clients.app.api.dev.adapter.GeneratedDtoApi;
import ru.edmebank.clients.app.api.service.GeneratedDtoService;
import ru.edmebank.contracts.dto.GenerateDtoRequest;

@Slf4j
@RestController
@Profile("local|dev")
@RequiredArgsConstructor
public class GenerateAdapter implements GeneratedDtoApi {

    private final GeneratedDtoService generatedDtoService;

    @Override
    public ResponseEntity<Object> generatedDto(@RequestBody GenerateDtoRequest generateDtoRequest) {
            Object obj = generatedDtoService.generatedDto(generateDtoRequest.type());
            return ResponseEntity.ok(obj);
    }
}
