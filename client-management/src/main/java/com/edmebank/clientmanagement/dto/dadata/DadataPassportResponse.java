package com.edmebank.clientmanagement.dto.dadata;

import lombok.Data;

@Data
public class DadataPassportResponse {
    private String source;
    private String series;
    private String number;
    private int qc;
}
