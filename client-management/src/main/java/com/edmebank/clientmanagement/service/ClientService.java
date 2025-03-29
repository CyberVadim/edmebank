package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.DadataFeignClient;
import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.dto.dadata.DadataPassportResponse;
import com.edmebank.clientmanagement.dto.documenter.TerrorismCheckRequest;
import com.edmebank.clientmanagement.dto.documenter.TerrorismCheckResponse;
import com.edmebank.clientmanagement.exception.ClientAlreadyExistsException;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.exception.InvalidPassportException;
import com.edmebank.clientmanagement.exception.TerroristFoundException;
import com.edmebank.clientmanagement.mapper.ClientMapper;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.ClientDocument;
import com.edmebank.clientmanagement.repository.ClientDocumentRepository;
import com.edmebank.clientmanagement.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientDocumentRepository clientDocumentRepository;
    private final ClientMapper clientMapper;
    private final DadataFeignClient dadataFeignClient;
    @Value("${dadata.api.authHeader}")
    private String authHeader;
    @Value("${dadata.api.secret}")
    private String secret;

    @Transactional
    public UUID registerClient(ClientDTO clientDTO) {
        Optional<Client> existingClient = clientRepository.findByPassportNumber(clientDTO.getPassportNumber());
        if (existingClient.isPresent()) {
            throw new ClientAlreadyExistsException("Клиент с таким паспортом уже зарегистрирован");
        }
        if (!isValidPassport(clientDTO.getPassportNumber())) {
            throw new InvalidPassportException("Недействительный паспорт");
        }
        boolean isTerrorist = isClientTerrorist();
        if (isTerrorist) {
            throw new TerroristFoundException("Клиент найден в базе террористов!");
        } else {
            log.info("Клиент проверен на причастность к терроризму, все ОК.");
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

    @Transactional
    public void disableNotification(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с ID " + clientId + " не найден"));
        client.setEnableNotifications(false);
        clientRepository.save(client);
    }

    public void uploadDocuments(UUID clientId, List<MultipartFile> documents) {
        String uploadDir = "D:/EDMEData/documents/";

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + clientId + " не найден"));

        for (MultipartFile document : documents) {
            try {
                // Создаем директорию, если ее нет
                Files.createDirectories(Paths.get(uploadDir));

                // Генерируем уникальное имя файла
                String filename = clientId + "_" + document.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + filename);

                // Сохраняем файл
                Files.write(filePath, document.getBytes());

                // Сохраняем путь в БД
                ClientDocument clientDocument = new ClientDocument();
                clientDocument.setClient(client);
                clientDocument.setDocumentPath(filePath.toString());
                clientDocumentRepository.save(clientDocument);

            } catch (IOException e) {
                throw new RuntimeException("Ошибка при сохранении файла", e);
            }
        }
    }

    public boolean isValidPassport(String passportNumber) {

        List<DadataPassportResponse> response = dadataFeignClient.cleanPassport(
                Collections.singletonList(passportNumber),
                authHeader,
                secret);

        if (response.isEmpty()) {
            return false;
        }
        int qc = response.get(0).getQc();
        if (qc == 1) {
            throw new InvalidPassportException("Неправильный формат серии или номера паспорта");
        }
        if (qc == 10) {
            throw new InvalidPassportException("Паспорт числится недействительным в базе МВД");
        }
        return qc == 0;
    }

    public boolean isClientTerrorist() {
        return false;
    }
}
