package ru.edmebank.contracts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.contracts.enums.AccountPriorityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityDetailsResponse {

    private UUID accountId;
    private AccountPriorityType accountType;
    private CurrentPriorities currentPriorities;
    private AllowedChanges allowedChanges;
    private LastUpdated lastUpdated;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentPriorities {
        private Boolean priorityForWriteOff;
        private Boolean priorityForAccrual;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllowedChanges {
        private Boolean canSetWriteOff;
        private Boolean canSetAccrual;
        private List<String> reasons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastUpdated {
        private LocalDateTime date;
        private String by;
    }
}