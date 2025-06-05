package ru.edmebank.print.utils;

import org.junit.jupiter.api.Test;
import ru.edmebank.print.app.exception.FontNotFoundException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FontUtilsTest {

    public static final String FONT_NOT_FOUND_MESSAGE = "Файл шрифта не найден";
    private static final String VALID_FONT_PATH = "fonts/times.ttf";
    private static final String MISSING_FONT_PATH = "fonts/not_exist.ttf";

    @Test
    void shouldLoadFontFileSuccessfully() {
        // given
        String fontPath = VALID_FONT_PATH;

        // when
        File fontFile = FontUtils.loadFont(fontPath);

        // then
        assertNotNull(fontFile);
        assertTrue(fontFile.exists());
    }

    @Test
    void shouldThrowFontNotFoundExceptionWhenResourceMissing() {
        // given
        String missingFont = MISSING_FONT_PATH;

        // when / then
        FontNotFoundException ex = assertThrows(FontNotFoundException.class, () -> {
            FontUtils.loadFont(missingFont);
        });

        assertTrue(ex.getMessage().contains(FONT_NOT_FOUND_MESSAGE));
    }
}

