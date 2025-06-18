package ru.edmebank.contracts.enums;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.compile;

public enum MaskingPattern {
    MASK("(.*)", matchResult -> "*****"),
    EMAIL("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+)", "***@$2"),
    PHONE("(\\+7\\(\\d{3}\\))\\d{3}-\\d{2}-(\\d{2})", "$1***-**-$2"),
    SNILS("(\\d{3})-\\d{3}-\\d{3}-\\d{2}", "$1-***-***-**"),
    PASSPORT_NUMBER("\\b\\d{6}\\b", "******"),
    PASSPORT_SERIES("\\b(\\d{2})\\d{2}\\b", "$1**"),
    PASSPORT_ISSUED_BY("((?:ГУ|ОВД|МВД|ФМС)(?:\\s+МВД)?)(.*)", "$1 ***"),
    SUBDIVISION_CODE("(\\d{3})-(\\d{3})", "$1-***"),
    DATE_DD_MM_YYYY("\\b(\\d{2})[-.](\\d{2})[-.](\\d{4})\\b", "**.**.$3"),
    DATE_YYYY_MM_DD("\\b(\\d{4})[-.](\\d{2})[-.](\\d{2})\\b", "$1-**-**"),
    INN_10_DIGITS("\\b(\\d{2})\\d{6}(\\d{2})\\b", "$1********$2"),
    INN_12_DIGITS("\\b(\\d{2})\\d{8}(\\d{2})\\b", "$1********$2"),
    POSTAL_CODE("\\b\\d{6}\\b", "******"),
    FULL_NAME("\\b[А-ЯЁ][а-яё]+\\b", "***"),
    ADDRESS("(.*)", matchResult -> {
        String address = matchResult.group(1);
        Pattern addressPattern = compile(
                "(?<POSTCODE>\\b\\d{6}\\b)" +
                        "|(?<CITY>\\b(?:г\\.\\s+|город\\s+)[^,]+)" +
                        "|(?<REGION>\\b[^,]*?(?:край|область|регион|республика)\\b[^,]*)" +
                        "|(?<DISTRICT>\\b[^,]*?(?:район|р-н)\\b[^,]*)" +
                        "|(?<STREET>(?i)(?:ул\\.?|улица|пер\\.?|переулок|проспект|пр-т)\\s*[^,]+)" +
                        "|(?<HOUSE>(?i)(?:д\\.?|дом)\\s*[^,]+)" +
                        "|(?<FLAT>(?i)(?:кв\\.?|квартира)\\s*[^,]+)",
                CASE_INSENSITIVE | UNICODE_CASE
        );
        return addressPattern.matcher(address).replaceAll(mr -> {
            Matcher matcher = (Matcher) mr;
            if (matcher.group("POSTCODE") != null) return "******";
            if (matcher.group("CITY") != null) return "г. ******";
            if (matcher.group("REGION") != null) return "*** ***";
            if (matcher.group("DISTRICT") != null) return "*** р-н";
            return matcher.group().replaceFirst("\\s+.+", " ***");
        });
    });
    
    private final String regex;
    private final Object replacement;
    private final Pattern compiledPattern;
    
    MaskingPattern(String regex, String replacement) {
        this.regex = regex;
        this.replacement = replacement;
        this.compiledPattern = compile(regex);
    }
    
    MaskingPattern(String regex, Function<MatchResult, String> replacement) {
        this.regex = regex;
        this.replacement = replacement;
        this.compiledPattern = compile(regex);
    }
    
    public String applyTo(String input) {
        if (input == null) return null;
        if (replacement instanceof String stringReplacement) {
            return compiledPattern.matcher(input).replaceAll(stringReplacement);
        } else {
            return compiledPattern.matcher(input)
                    .replaceAll((Function<MatchResult, String>) replacement);
        }
    }
}
