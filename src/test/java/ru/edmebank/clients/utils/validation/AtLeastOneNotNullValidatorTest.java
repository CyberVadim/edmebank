package ru.edmebank.clients.utils.validation;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.edmebank.clients.app.api.repository.SpousesRepository;
import ru.edmebank.clients.domain.entity.Spouses;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AtLeastOneNotNullValidatorTest {

    @Autowired
    private SpousesRepository spousesRepository;

    @Test
    void shouldThrowValidationExceptionWhenAllAnnotatedFieldsAreNull() {
        Spouses spouses = new Spouses();
        spouses.setMarriageDate(LocalDate.now());

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> spousesRepository.saveAndFlush(spouses)
        );

        String message = exception.getMessage();
        assertTrue(
                message.contains("хотя бы одно из них: spouseClientId, fullName"),
                "Ожидалось сообщение о нарушении, но оно не корректно. Фактическое: " + message
        );
    }
}