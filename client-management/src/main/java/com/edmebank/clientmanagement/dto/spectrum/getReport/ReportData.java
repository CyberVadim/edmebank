package com.edmebank.clientmanagement.dto.spectrum.getReport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportData {
    private String uid;
    @JsonProperty("domain_uid")
    private String domainUid;
    @JsonProperty("report_type_uid")
    private String reportTypeUid;
    private QueryData query;
    private StateData state;
}
