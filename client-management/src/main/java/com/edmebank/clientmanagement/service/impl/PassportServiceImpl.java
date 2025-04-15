package com.edmebank.clientmanagement.service.impl;

import com.edmebank.clientmanagement.dto.PassportDto;
import com.edmebank.clientmanagement.mapper.PassportMapper;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.Passport;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.PassportRepository;
import com.edmebank.clientmanagement.service.PassportService;
import com.edmebank.clientmanagement.util.PassportOcrResult;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Клиент не найден"));

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = Paths.get(uploadDir).resolve(fileName);
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination);

            PassportOcrResult passportOcrResult = ocrService.extractText(destination.toFile());

            Passport passport = new Passport();
            passport.setClient(client);
            passport.setDocumentPath(destination.toString());
            passport.setPassportNumber(passportOcrResult.passportNumber);
            passport.setPassportIssueDate(passportOcrResult.issueDate);
            passport.setUploadedAt(LocalDate.now());

            return passportMapper.toDto(passportRepository.save(passport));

        } catch (IOException e) {
            log.info("Ошибка при сохранении файла");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<PassportDto> getPassportsByClientId(UUID clientId) {
        return passportRepository.findAllByClientId(clientId).stream()
                .map(passportMapper::toDto)
                .collect(Collectors.toList());
    }
}

