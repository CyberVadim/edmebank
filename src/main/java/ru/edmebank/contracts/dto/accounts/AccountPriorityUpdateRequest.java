package ru.edmebank.contracts.dto.accounts;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityUpdateRequest {

    @NotNull(message = "Флаг приоритета для списания не может быть пустым")
    private Boolean priorityForWriteOff;

    @NotNull(message = "Флаг приоритета для начисления не может быть пустым")
    private Boolean priorityForAccrual;

    @NotBlank(message = "Причина изменения приоритетов не может быть пустой")
    @Size(max = 500, message = "Причина изменения приоритетов не может превышать 500 символов")
    private String reason;

    @NotNull(message = "Информация об инициаторе не может быть пустой")
    @Valid
    private Initiator initiator;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Initiator {
        @NotBlank(message = "ID инициатора не может быть пустым")
        private String id;

        @NotBlank(message = "ФИО инициатора не может быть пустым")
        private String name;

        @NotNull(message = "Роль инициатора не может быть пустой")
        private InitiatorRole role;
    }

    public enum InitiatorRole {
        CREDIT_MANAGER,
        CLIENT,
        SYSTEM
    }
}