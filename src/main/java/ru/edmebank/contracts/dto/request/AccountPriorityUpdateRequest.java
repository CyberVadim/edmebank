package ru.edmebank.contracts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.contracts.enums.InitiatorRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityUpdateRequest {

    @NotNull(message = "Флаг приоритета для списания обязателен")
    private Boolean priorityForWriteOff;

    @NotNull(message = "Флаг приоритета для начисления обязателен")
    private Boolean priorityForAccrual;

    @NotBlank(message = "Причина изменения обязательна")
    @Size(max = 500, message = "Причина изменения не должна превышать 500 символов")
    private String reason;

    @NotNull(message = "Информация об инициаторе обязательна")
    @Valid
    private Initiator initiator;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Initiator {
        @NotBlank(message = "ID инициатора обязателен")
        private String id;

        @NotBlank(message = "ФИО инициатора обязательно")
        private String name;

        @NotNull(message = "Роль инициатора обязательна")
        private InitiatorRole role;
    }
}