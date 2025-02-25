package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.DadataFeignClient;
import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.dto.dadata.DadataPassportResponse;
import com.edmebank.clientmanagement.exception.ClientAlreadyExistsException;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.exception.InvalidPassportException;
import com.edmebank.clientmanagement.mapper.ClientMapper;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final DadataFeignClient dadataFeignClient;

    @Transactional
    public UUID registerClient(ClientDTO clientDTO) {
        Optional<Client> existingClient = clientRepository.findByPassportNumber(clientDTO.getPassportNumber());
        if (existingClient.isPresent()) {
            throw new ClientAlreadyExistsException("Клиент с таким паспортом уже зарегистрирован");
        }
        String passportNumber = clientDTO.getPassportSeries() + clientDTO.getPassportNumber();

        if (!isValidPassport(passportNumber)) {
            throw new IllegalArgumentException("Недействительный паспорт");
        }


        Client client = clientMapper.toEntity(clientDTO);
        client.setAmlChecked(true); // AML проверка выполнена, временная заглушка
        client = clientRepository.save(client);

        return client.getId();
    }

    @Transactional
    public void updateClient(UUID clientId, ClientDTO clientDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + clientId + " не найден"));

        clientMapper.updateClientFromDto(clientDTO, client);

        clientRepository.save(client);
    }

    public boolean isValidPassport(String passportNumber) {
        String token = "Token 50353c0408a3f1a0c2805bd8e0342a2d11a394f2";
        String secret = "b7d373eed05ca1b9f3d8550913b1ae99f07fae34";

        List<DadataPassportResponse> response = dadataFeignClient.cleanPassport(
                Collections.singletonList(passportNumber),
//                List.of(passportNumber),
                token,
                secret);

        if (response.isEmpty()) {
            return false; // Dadata не вернуло данные
        }

        int qc = response.get(0).getQc();
        if (qc == 1) {
            throw new InvalidPassportException("Неправильный формат серии или номера паспорта");
        }
        if (qc == 10) {
            throw new InvalidPassportException("Паспорт числится недействительным в базе МВД");
        }
        return qc == 0; // Паспорт действителен
    }
}
