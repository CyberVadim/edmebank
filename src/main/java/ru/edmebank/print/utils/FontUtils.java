package ru.edmebank.print.utils;

import ru.edmebank.print.app.exception.FontLoadException;
import ru.edmebank.print.app.exception.FontNotFoundException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FontUtils {

    public static File loadFont(String fontPathInResources) {
        try {
            URL fontUrl = FontUtils.class.getClassLoader().getResource(fontPathInResources);
            if (fontUrl == null) {
                throw new FontNotFoundException("Файл шрифта не найден: " + fontPathInResources);
            }

            File fontFile = new File(fontUrl.toURI());
            if (!fontFile.exists()) {
                throw new FontLoadException("Файл не существует: " + fontFile.getAbsolutePath());
            }

            return fontFile;

        } catch (URISyntaxException e) {
            throw new FontLoadException("Некорректный URI для шрифта: " + fontPathInResources, e);
        }
    }
}
