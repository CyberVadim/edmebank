package ru.edmebank.contracts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TemplateType {
    UNIVERSAL_PERSONAL_DATA,
    /**
     * @deprecated Используется только в юнит-тестах. Не использовать в production-логике.
     */
    @Deprecated
    FOR_TEST_DATA;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TemplateType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TemplateType не может быть null");
        }

        return Arrays.stream(values())
                .filter(t -> t.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип шаблона: " + value));
    }
}