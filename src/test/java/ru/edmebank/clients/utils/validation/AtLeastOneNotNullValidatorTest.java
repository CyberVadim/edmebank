package ru.edmebank.clients.utils.validation;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edmebank.clients.app.api.repository.SpousesRepository;
import ru.edmebank.clients.domain.entity.Spouses;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AtLeastOneNotNullValidatorTest {

    @Mock
    private SpousesRepository spousesRepository;

    @Test
    void shouldThrowValidationExceptionWhenAllAnnotatedFieldsAreNull() {
        Spouses spouses = new Spouses();
        spouses.setMarriageDate(LocalDate.now());

        when(spousesRepository.saveAndFlush(any(Spouses.class)))
                .thenThrow(new ConstraintViolationException("Validation failed", null));

        assertThrows(ConstraintViolationException.class, () -> spousesRepository.saveAndFlush(spouses));
    }
}