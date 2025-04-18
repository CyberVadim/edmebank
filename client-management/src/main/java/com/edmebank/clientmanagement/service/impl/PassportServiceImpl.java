package com.edmebank.clientmanagement.service.impl;

import com.edmebank.clientmanagement.dto.PassportDto;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.exception.PassportOcrException;
import com.edmebank.clientmanagement.exception.PassportStorageException;
import com.edmebank.clientmanagement.mapper.PassportMapper;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.Passport;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.PassportRepository;
import com.edmebank.clientmanagement.service.PassportService;
import com.edmebank.clientmanagement.util.PassportOcrResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportServiceImpl implements PassportService {

    private final PassportRepository passportRepository;
    private final PassportMapper passportMapper;
    private final ClientRepository clientRepository;
    private final OcrServiceImpl ocrService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public PassportDto uploadPassport(UUID clientId, MultipartFile file) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = Paths.get(uploadDir).resolve(fileName);
        try {
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла", e);
            throw new PassportStorageException("Не удалось сохранить файл паспорта", e);
        }

        PassportOcrResult passportOcrResult = ocrService.extractText(destination.toFile());

        if (passportOcrResult.passportNumber == null) {
            throw new PassportOcrException("Не удалось распознать номер паспорта. Проверьте качество изображения.");
        }

        if (passportOcrResult.issueDate == null) {
            throw new PassportOcrException("Не удалось распознать дату выдачи паспорта. Проверьте качество изображения.");
        }

        Passport passport = new Passport();
        passport.setClient(client);
        passport.setDocumentPath(destination.toString());
        passport.setPassportNumber(passportOcrResult.passportNumber);
        passport.setPassportIssueDate(passportOcrResult.issueDate);
        passport.setUploadedAt(LocalDate.now());

        return passportMapper.toDto(passportRepository.save(passport));
    }
}

