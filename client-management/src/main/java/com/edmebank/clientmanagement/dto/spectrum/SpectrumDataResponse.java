package com.edmebank.clientmanagement.dto.spectrum;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class SpectrumDataResponse {
    private String state;
    private List<ReportData> data;

    @Builder
    @Data
    public static class ReportData {
        private String uid;
        private boolean isnew;
        private String process_request_uid;
        private String suggest_get;
        private boolean hasUnfulfilledCourtDecisions;
        private boolean hasSanctionedConnections;
        private boolean hasSuspiciousFinancialHistory;
    }
}
