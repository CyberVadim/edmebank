package ru.edmebank.clients.adapter.input.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.edmebank.clients.app.api.UiClientApi;
import ru.edmebank.contracts.dto.ClientUiDto;

@RestController
public class ClientAdapter implements UiClientApi {
    @Override
    public ResponseEntity<byte[]> completeCreateClient(ClientUiDto clientUiDto) throws Exception {
        System.out.println(clientUiDto);
        return ResponseEntity.ok(new byte[1]);
    }
}
