package ru.edmebank.print.app.impl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.edmebank.contracts.enums.TemplateType;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StrategySelectorTest {

    private PdfTemplateStrategy mockStrategy;
    private StrategySelector selector;

    @BeforeEach
    void setUp() {
        // given — подготовка стратегии и селектора
        mockStrategy = mock(PdfTemplateStrategy.class);
        selector = new StrategySelector(Map.of(
                TemplateType.UNIVERSAL_PERSONAL_DATA.name(), mockStrategy
        ));
    }

    @Test
    void shouldReturnStrategyWhenTypeExists() {
        // when
        PdfTemplateStrategy result = selector.getStrategy(TemplateType.UNIVERSAL_PERSONAL_DATA);

        // then
        assertNotNull(result);
        assertEquals(mockStrategy, result);
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        // when / then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            selector.getStrategy(null);
        });

        assertEquals("Тип шаблона не передан", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStrategyNotFound() {
        // when / then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            selector.getStrategy(TemplateType.FOR_TEST_DATA); // предположим, что в Map его нет
        });

        assertEquals("Неподдерживаемый тип шаблона: FOR_TEST_DATA", ex.getMessage());
    }
}

