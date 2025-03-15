package com.edmebank.clientmanagement.dto.spectrum.getReport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SourceData {
    @JsonProperty("_id")
    private String _id;
    private String state;
    private Map<String, Object> data;
}
