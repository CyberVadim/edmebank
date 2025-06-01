package ru.edmebank.print.utils;

import org.junit.jupiter.api.Test;
import ru.edmebank.print.app.exception.FontNotFoundException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FontUtilsTest {

    @Test
    void shouldLoadFontFileSuccessfully() {
        // given
        String fontPath = "fonts/times.ttf";

        // when
        File fontFile = FontUtils.loadFont(fontPath);

        // then
        assertNotNull(fontFile);
        assertTrue(fontFile.exists());
    }

    @Test
    void shouldThrowFontNotFoundExceptionWhenResourceMissing() {
        // given
        String missingFont = "fonts/not_exist.ttf";

        // when / then
        FontNotFoundException ex = assertThrows(FontNotFoundException.class, () -> {
            FontUtils.loadFont(missingFont);
        });

        assertTrue(ex.getMessage().contains("Файл шрифта не найден"));
    }
}

