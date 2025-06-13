package ru.edmebank.accounts.adapter.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.accounts.domain.entity.AccountType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityGetResponse {

    @Builder.Default
    private String status = "SUCCESS";

    private AccountPriorityGetData data;

    private AccountPriorityResponse.ErrorData error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountPriorityGetData {
        private String accountId;
        private AccountType accountType;
        private CurrentPriorities currentPriorities;
        private AllowedChanges allowedChanges;
        private LastUpdated lastUpdated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentPriorities {
        private Boolean priorityForWriteOff;
        private Boolean priorityForAccrual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllowedChanges {
        private Boolean canSetWriteOff;
        private Boolean canSetAccrual;
        private List<String> reasons;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LastUpdated {
        private LocalDateTime date;
        private String by;
    }

    public static AccountPriorityGetResponse success(AccountPriorityGetData data) {
        return AccountPriorityGetResponse.builder()
                .status("SUCCESS")
                .data(data)
                .build();
    }

    public static AccountPriorityGetResponse error(String code, String message, Object details) {
        return AccountPriorityGetResponse.builder()
                .status("ERROR")
                .error(AccountPriorityResponse.ErrorData.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }
}