package com.edmebank.clientmanagement.service.impl;

import com.edmebank.clientmanagement.service.OcrService;
import com.edmebank.clientmanagement.util.PassportImageProcessor;
import com.edmebank.clientmanagement.util.PassportOcrResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    @Override
    public PassportOcrResult extractText(File imageFile) {
        try {
            PassportOcrResult result =
                    PassportImageProcessor.extractPassportData(imageFile);

            log.info("📄 Номер паспорта: {}", result.passportNumber);
            log.info("📅 Дата выдачи: {}", result.issueDate);
            return result;

        } catch (Exception e) {
            log.error("❌ Ошибка OCR: {}", e.getMessage(), e);
            throw new RuntimeException("OCR ошибка: " + e.getMessage(), e);
        }
    }
}
