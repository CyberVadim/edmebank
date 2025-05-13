package ru.edmebank.contracts.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Назначение: Категории клиентов
 */
@AllArgsConstructor
@Getter
@Slf4j
public enum ClientCategoryEnum {
    VIP(1, "VIP"),
    STANDARD(2, "STANDARD"),
    STUDENT(3, "STUDENT"),
    PENSIONER(4, "PENSIONER");

    private final int id;
    private final String code;

    private static final Map<String, ClientCategoryEnum> CODE_MAP = Stream.of(values())
            .collect(Collectors.toMap(ClientCategoryEnum::getCode, e -> e));

    public static ClientCategoryEnum fromCode(String code) {
        ClientCategoryEnum clientCategoryEnum = CODE_MAP.get(code.toUpperCase());
        if (clientCategoryEnum == null) {
            log.warn("Unknown enum code {}, returning default: STANDARD", code);
            return STANDARD;
        }
        return clientCategoryEnum;
    }
}
