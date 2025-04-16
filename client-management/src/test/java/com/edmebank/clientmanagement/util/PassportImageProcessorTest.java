package com.edmebank.clientmanagement.util;

import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PassportImageProcessorTest {

    private static Tesseract tesseract;

    @BeforeAll
    static void setup() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("rus");
    }

    @Test
    void testExtractPassportData() throws Exception {
        File testImage = new File("src/test/resources/passport_sample.jpg");
        PassportOcrResult result =
                PassportImageProcessor.extractPassportData(testImage);

        assertNotNull(result.passportNumber, "Номер паспорта должен быть извлечён");
        assertTrue(result.passportNumber.matches("\\d{10}"), "Номер должен состоять из 10 цифр");
        assertEquals("1708885361", result.passportNumber, "Номер паспорта должен быть точным");

        assertNotNull(result.issueDate, "Дата выдачи должна быть извлечена");
        assertTrue(result.issueDate.isBefore(LocalDate.now()), "Дата должна быть в прошлом");
        assertEquals(LocalDate.of(1988, 11, 9), result.issueDate, "Дата выдачи должна совпадать");

        System.out.println("📄 Распознанный номер: " + result.passportNumber);
        System.out.println("📅 Дата выдачи: " + result.issueDate);
    }
}

