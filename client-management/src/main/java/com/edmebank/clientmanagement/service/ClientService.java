package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.client.DadataFeignClient;
import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.exception.AmlCheckedException;
import com.edmebank.clientmanagement.exception.ClientAlreadyExistsException;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.exception.InvalidPassportException;
import com.edmebank.clientmanagement.mapper.ClientMapper;
import com.edmebank.clientmanagement.model.Client;
//import com.edmebank.clientmanagement.model.ClientDocument;
import com.edmebank.clientmanagement.repository.PassportRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PassportRepository passportRepository;
    private final ClientMapper clientMapper;
    private final DadataFeignClient dadataFeignClient;
    private final SpectrumService spectrumService;
    private final PassportValidationService passportValidationService;
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
        if (!passportValidationService.isValid(clientDTO.getPassportNumber())) {
            throw new InvalidPassportException("Проверка паспорта не пройдена");
        } else {
            log.info("Проверка паспорта прошла успешно");
            boolean isAmlChecked = spectrumService.canRegisterClient(clientDTO);
            if (isAmlChecked) {
                log.info("Клиент прошел проверку на благонадежность, все ОК.");
                Client client = clientMapper.toEntity(clientDTO);
                client.setAmlChecked(true);
                client = clientRepository.save(client);
                return client.getId();
            } else {
                throw new AmlCheckedException("Клиент не прошел проверку на благонадежность");
            }
        }
    }

    @Transactional
    public void updateClient(UUID clientId, ClientDTO clientDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + clientId + " не найден"));

        clientMapper.updateClientFromDto(clientDTO, client);
        clientRepository.save(client);
    }

    @Transactional
    public void patchClient(UUID clientId, ClientDTO clientDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с ID " + clientId + " не найден"));
        if (clientDTO.getFirstName() != null) {
            client.setFirstName(clientDTO.getFirstName());
        }
        if (clientDTO.getLastName() != null) {
            client.setLastName(clientDTO.getLastName());
        }
        if (clientDTO.getMiddleName() != null) {
            client.setMiddleName(clientDTO.getMiddleName());
        }
        if (clientDTO.getAddress() != null) {
            client.setAddress(clientDTO.getAddress());
        }
        if (clientDTO.getPhone() != null) {
            client.setPhone(clientDTO.getPhone());
        }
        if (clientDTO.getEmail() != null) {
            client.setEmail(clientDTO.getEmail());
        }
        if (clientDTO.getPassportIssuedBy() != null) {
            client.setPassportIssuedBy(clientDTO.getPassportIssuedBy());
        }
        if (clientDTO.getPassportIssueDate() != null) {
            client.setPassportIssueDate(clientDTO.getPassportIssueDate());
        }
        if (clientDTO.getDateOfBirth() != null) {
            client.setDateOfBirth(clientDTO.getDateOfBirth());
            client.calculatePassportExpiryDate();
        }
        if (clientDTO.getPassportNumber() != null) {
            Optional<Client> existingClient = clientRepository.findByPassportNumber(clientDTO.getPassportNumber());
            if (existingClient.isPresent()) {
                throw new ClientAlreadyExistsException("Клиент с таким паспортом уже зарегистрирован");
            }
            if (!passportValidationService.isValid(clientDTO.getPassportNumber())) {
                throw new InvalidPassportException("Проверка паспорта не пройдена");
            } else {
                log.info("Проверка паспорта прошла успешно");
                boolean isAmlChecked = spectrumService.canRegisterClient(clientDTO);
                if (isAmlChecked) {
                    client.setPassportNumber(clientDTO.getPassportNumber());
                } else {
                    throw new AmlCheckedException("Клиент не прошел проверку на благонадежность");
                }
            }
        }
        clientRepository.save(client);
    }

    @Transactional
    public void disableNotification(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с ID " + clientId + " не найден"));
        client.setEnableNotifications(false);
        clientRepository.save(client);
    }

//    public void uploadDocuments(UUID clientId, List<MultipartFile> documents) {
//        String uploadDir = "D:/EDMEData/documents/";
//
//        Client client = clientRepository.findById(clientId)
//                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + clientId + " не найден"));
//
//        for (MultipartFile document : documents) {
//            try {
//                // Создаем директорию, если ее нет
//                Files.createDirectories(Paths.get(uploadDir));
//
//                // Генерируем уникальное имя файла
//                String filename = clientId + "_" + document.getOriginalFilename();
//                Path filePath = Paths.get(uploadDir + filename);
//
//                // Сохраняем файл
//                Files.write(filePath, document.getBytes());
//
//                // Сохраняем путь в БД
//                ClientDocument clientDocument = new ClientDocument();
//                clientDocument.setClient(client);
//                clientDocument.setDocumentPath(filePath.toString());
//                passportRepository.save(clientDocument);
//
//            } catch (IOException e) {
//                throw new RuntimeException("Ошибка при сохранении файла", e);
//            }
//        }
//    }
}
