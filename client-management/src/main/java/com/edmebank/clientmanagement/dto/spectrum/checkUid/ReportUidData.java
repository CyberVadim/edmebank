package com.edmebank.clientmanagement.dto.spectrum.checkUid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportUidData {
    private String uid;
    private boolean isnew;

    @JsonProperty("suggest_get")
    private String suggestGet;
}
