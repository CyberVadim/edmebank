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

    @JsonCreator
    public static TemplateType from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value) || v.name().replace("_", "").equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown template type: " + value));
    }
}

