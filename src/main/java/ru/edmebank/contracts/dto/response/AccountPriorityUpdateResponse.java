package ru.edmebank.contracts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityUpdateResponse {

    private UUID accountId;
    private UpdatedPriorities updatedPriorities;
    private LocalDateTime updatedAt;
    private Integer version;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatedPriorities {
        private Boolean priorityForWriteOff;
        private Boolean priorityForAccrual;
    }
}