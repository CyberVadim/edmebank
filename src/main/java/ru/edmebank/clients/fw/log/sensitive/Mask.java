package ru.edmebank.clients.fw.log.sensitive;

import ru.edmebank.contracts.enums.MaskingPattern;

public record Mask(Object args, MaskingPattern[] patterns) {
    public static Mask sensitive(Object value, MaskingPattern pattern) {
        return new Mask(value, new MaskingPattern[] {pattern});
    }
    
    public static Mask sensitive(Object value, MaskingPattern... patterns) {
        return new Mask(value, patterns);
    }
}
