package com.edmebank.clientmanagement.dto.spectrum.getReport;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private String state;
    private int size;
    private String version;
    private String stamp;
    private List<ReportData> data;
}

