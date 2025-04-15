package com.edmebank.clientmanagement.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassportImageProcessor {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static PassportOcrResult extractPassportData(File imageFile, Tesseract tesseract) throws IOException, TesseractException {
        // 1. Обрабатываем основное изображение
        File processed = preprocess(imageFile);
        String text = tesseract.doOCR(processed);

        // 2. Извлекаем дату
        LocalDate issueDate = extractIssueDate(text);

        // 3. Обрабатываем зону номера
        BufferedImage rotatedNumber = extractAndRotatePassportNumber(imageFile);
        File numberFile = File.createTempFile("number_rotated_", ".png");
        ImageIO.write(rotatedNumber, "png", numberFile);

        String numberText = tesseract.doOCR(numberFile);
        String passportNumber = extractPassportNumber(numberText);

        // 4. Возвращаем результат
        PassportOcrResult result = new PassportOcrResult();
        result.rawText = text + "\n---NUMERIC ZONE---\n" + numberText;
        result.issueDate = issueDate;
        result.passportNumber = passportNumber;
        return result;
    }

    private static File preprocess(File originalFile) throws IOException {
        BufferedImage original = ImageIO.read(originalFile);

        // ч/б
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();

        // контраст
        RescaleOp rescaleOp = new RescaleOp(1.5f, 0, null);
        rescaleOp.filter(grayscale, grayscale);

        File temp = File.createTempFile("preprocessed_", ".png");
        ImageIO.write(grayscale, "png", temp);
        return temp;
    }

    private static BufferedImage extractAndRotatePassportNumber(File originalFile) throws IOException {
        BufferedImage original = ImageIO.read(originalFile);

        // Вырезаем правую вертикальную область (подгоняется под изображение)
        int x = original.getWidth() - 90;
        int y = 0;
        int width = 80;
        int height = original.getHeight();

        BufferedImage numberRegion = original.getSubimage(x, y, width, height);

        // Поворот на 90 градусов
        int w = numberRegion.getWidth();
        int h = numberRegion.getHeight();
        BufferedImage rotated = new BufferedImage(h, w, numberRegion.getType());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rotated.setRGB(j, w - i - 1, numberRegion.getRGB(i, j));
            }
        }

        return rotated;
    }

    private static String extractPassportNumber(String text) {
        // 17 08 885361 → 1708885361
        Matcher matcher = Pattern.compile("([0-9]{2}\\s?[0-9]{2}\\s?[0-9]{6})").matcher(text.replaceAll("\\s+", " "));
        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+", "");
        }
        return null;
    }

    private static LocalDate extractIssueDate(String text) {
        Matcher matcher = Pattern.compile("([0-9]{2}\\.[0-9]{2}\\.[0-9]{4})").matcher(text);
        if (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group(1), DATE_FORMAT);
            } catch (Exception ignored) {}
        }
        return null;
    }
}

