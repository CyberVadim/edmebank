package ru.edmebank.contracts.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityDetailsResponse {
    private String status;
    private PriorityData data;
    private ErrorData error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityData {
        private String accountId;
        private String accountType;
        private CurrentPriorities currentPriorities;
        private AllowedChanges allowedChanges;
        private LastUpdated lastUpdated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentPriorities {
        private Boolean priorityForWriteOff;
        private Boolean priorityForAccrual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllowedChanges {
        private Boolean canSetWriteOff;
        private Boolean canSetAccrual;
        private List<String> reasons;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastUpdated {
        private ZonedDateTime date;
        private String by;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorData {
        private String code;
        private String message;
        private Map<String, Object> details;
        private ZonedDateTime timestamp;
    }
}