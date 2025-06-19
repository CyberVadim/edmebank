package ru.edmebank.contracts.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityResponse {

    @Builder.Default
    private String status = "SUCCESS";

    private AccountPriorityData data;

    private ErrorData error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountPriorityData {
        private String accountId;

        private UpdatedPriorities updatedPriorities;

        private LocalDateTime updatedAt;

        private Long version;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdatedPriorities {
        private Boolean priorityForWriteOff;
        private Boolean priorityForAccrual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorData {
        private String code;
        private String message;
        private Object details;
        private LocalDateTime timestamp;
    }

    public static AccountPriorityResponse success(String accountId, Boolean priorityForWriteOff,
                                                  Boolean priorityForAccrual, LocalDateTime updatedAt, Long version) {
        return AccountPriorityResponse.builder()
                .status("SUCCESS")
                .data(AccountPriorityData.builder()
                        .accountId(accountId)
                        .updatedPriorities(UpdatedPriorities.builder()
                                .priorityForWriteOff(priorityForWriteOff)
                                .priorityForAccrual(priorityForAccrual)
                                .build())
                        .updatedAt(updatedAt)
                        .version(version)
                        .build())
                .build();
    }

    public static AccountPriorityResponse error(String code, String message, Object details) {
        return AccountPriorityResponse.builder()
                .status("ERROR")
                .error(ErrorData.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }
}