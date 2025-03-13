package com.edmebank.clientmanagement.dto.spectrum.getReport;

import lombok.Data;

import java.util.Map;

@Data
public class QueryData {
    private String type;
    private String body;
    private Map<String, String> data;
}
