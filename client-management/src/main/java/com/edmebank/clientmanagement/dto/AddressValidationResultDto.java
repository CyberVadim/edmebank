package com.edmebank.clientmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationResultDto {
    private String fullAddress;
    private String countryCode;
    
    private Map<String, String> garLevels = new LinkedHashMap<>();
    private Map<String, String> garParams = new LinkedHashMap<>();
    
    private int confidence;
    private String error;
    private List<String> garGuids;
}
