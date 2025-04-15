package com.edmebank.clientmanagement.service.impl;

import com.edmebank.clientmanagement.service.OcrService;
import com.edmebank.clientmanagement.util.PassportImageProcessor;
import com.edmebank.clientmanagement.util.PassportOcrResult;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    private final Tesseract tesseract;

    public OcrServiceImpl() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("E:\\MyApp\\EDME\\edmebank\\client-management\\tessdata"); // путь до папки с языковыми файлами
        this.tesseract.setLanguage("rus"); // для русского текста
    }

    @Override
    public PassportOcrResult extractText(File imageFile) {
        try {
            PassportOcrResult result =
                    PassportImageProcessor.extractPassportData(imageFile, tesseract);

            log.info("Номер паспорта: {}", result.passportNumber);
            log.info("Дата выдачи: {}", result.issueDate);
            log.info("Распознанный текст:\n{}", result.rawText);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("OCR ошибка: " + e.getMessage(), e);
        }
    }

}

