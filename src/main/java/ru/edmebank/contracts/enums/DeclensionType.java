package ru.edmebank.contracts.enums;

import lombok.AllArgsConstructor;

import static java.lang.String.format;

@AllArgsConstructor
public enum DeclensionType {
    SINGULAR_NOMINATIVE("календарный день"),
    SINGULAR_GENITIVE("календарного дня"),
    SINGULAR_DATIVE("календарному дню"),
    SINGULAR_ACCUSATIVE("календарный день"),
    SINGULAR_INSTRUMENTAL("календарным днём"),
    SINGULAR_PREPOSITIONAL("календарном дне"),

    PLURAL_NOMINATIVE("календарные дни"),
    PLURAL_GENITIVE("календарных дней"),
    PLURAL_DATIVE("календарным дням"),
    PLURAL_ACCUSATIVE("календарных дней"),
    PLURAL_INSTRUMENTAL("календарными днями"),
    PLURAL_PREPOSITIONAL("календарных днях");

    public final String calendarDay;

    public String getFormattedString(int number) {
        return format("%d %s", number, calendarDay);
    }
}
