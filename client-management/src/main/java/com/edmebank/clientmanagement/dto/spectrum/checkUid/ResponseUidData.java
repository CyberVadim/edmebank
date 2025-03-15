package com.edmebank.clientmanagement.dto.spectrum.checkUid;

import lombok.Data;

import java.util.List;

@Data
public class ResponseUidData {
    private String state;
    private int size;
    private String version;
    private String stamp;
    private List<ReportUidData> data;
}

