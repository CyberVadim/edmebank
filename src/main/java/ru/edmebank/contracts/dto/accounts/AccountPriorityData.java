package ru.edmebank.contracts.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityData implements Serializable {
    private UUID accountId;
    private Boolean priorityForWriteOff = false;
    private Boolean priorityForAccrual = false;
    private String lastUpdatedBy;
    private String lastUpdatedRole;
    private String lastUpdateReason;
    private Integer version = 0;
    private LocalDateTime updatedAt = LocalDateTime.now();
}