package ru.edmebank.contracts.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPriorityRequest {
    @NotNull(message = "Поле priorityForWriteOff обязательно")
    private Boolean priorityForWriteOff;

    @NotNull(message = "Поле priorityForAccrual обязательно")
    private Boolean priorityForAccrual;

    @NotNull(message = "Поле reason обязательно")
    @Size(max = 500, message = "Максимальная длина причины 500 символов")
    private String reason;

    @NotNull(message = "Поле initiator обязательно")
    @Valid
    private Initiator initiator;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Initiator {
        @NotNull(message = "Поле id обязательно")
        private String id;

        @NotNull(message = "Поле name обязательно")
        private String name;

        @NotNull(message = "Поле role обязательно")
        private String role;
    }
}