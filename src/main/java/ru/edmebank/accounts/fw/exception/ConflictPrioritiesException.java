package ru.edmebank.accounts.fw.exception;

import lombok.Getter;

@Getter
public class ConflictPrioritiesException extends AccountException {

    public ConflictPrioritiesException() {
        super("Конфликт приоритетов. Попытка установить оба приоритета одновременно", "CONFLICT_PRIORITIES");
    }
}