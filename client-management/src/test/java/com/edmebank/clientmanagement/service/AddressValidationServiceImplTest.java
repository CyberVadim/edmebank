package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.AddressValidationResultDto;
import com.pullenti.address.AddressService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressValidationServiceImplTest {
    
    @BeforeAll
    static void initPullenti() throws Exception {
        AddressService.initialize();
        String garIndexPath = AddressValidationServiceImplTest.class
                .getClassLoader()
                .getResource("Pullenti/Gar77")
                .getPath();
        AddressService.setGarIndexPath(garIndexPath);
    }
    
    @Test
    void shouldValidateAndNormalizeAddress() {
        AddressValidationServiceImpl addressValidationService = new AddressValidationServiceImpl();
        String address = "улица Вавилова, 39А, Москва";
        // When
        AddressValidationResultDto result = addressValidationService.validate(address);
        
        // Then
        assertNotNull(result);
        assertEquals("Россия, 117312, город Москва, улица Вавилова, д.39А", result.getFullAddress());
        assertEquals("RU", result.getCountryCode());
        assertTrue(result.getConfidence() >= 80);
        
        // Verify GAR levels
        assertEquals("город Москва", result.getGarLevels().get("REGION"));
        assertEquals("улица Вавилова", result.getGarLevels().get("STREET"));
        assertEquals("д.39А", result.getGarLevels().get("BUILDING"));
        assertEquals("муниципальный округ Академический", result.getGarLevels().get("MUNICIPALAREA"));
    }
}
