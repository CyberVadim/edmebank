package ru.edmebank.clients.fw.log.sensitive;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import ru.edmebank.clients.domain.annotation.SensitiveField;
import ru.edmebank.clients.domain.annotation.SensitiveObject;
import ru.edmebank.contracts.enums.MaskingPattern;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;
import static java.lang.System.identityHashCode;
import static java.lang.ThreadLocal.withInitial;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Set.of;
import static java.util.concurrent.TimeUnit.HOURS;

public class SensitiveDataMasker {
    private static final ThreadLocal<Set<Integer>> VISITED = withInitial(HashSet::new);
    private static final Cache<Class<?>, Map<String, Field>> SENSITIVE_FIELD_CACHE =
            newBuilder()
                    .maximumSize(1000)
                    .expireAfterAccess(1, HOURS)
                    .build();
    private static final Cache<Field, MaskingPattern[]> PATTERN_CACHE =
            newBuilder()
                    .maximumSize(2000)
                    .build();
    private static final Set<Class<?>> SIMPLE_TYPES = of(
            String.class, Boolean.class, Character.class, Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class
    );
    
    public static Object mask(Object obj) {
        try {
            if (obj == null) return null;
            if (obj instanceof Mask mask) return maskWithPatterns(mask.args(), mask.patterns());
            if (isSimpleType(obj)) return obj;
            if (obj instanceof LocalDate date) return date.toString();
            return maskObject(obj);
        } catch (Exception e) {
            return "[complex object]";
        }
    }
    
    public static Object mask(Object obj, MaskingPattern pattern) {
        if (obj == null) return null;
        if (obj instanceof LocalDate date) return pattern.applyTo(date.format(ISO_LOCAL_DATE));
        if (obj instanceof String value) return pattern.applyTo(value);
        return mask(obj);
    }
    
    public static Object maskWithPatterns(Object obj, MaskingPattern[] patterns) {
        if (obj == null || patterns == null || patterns.length == 0) return mask(obj);
        if (obj instanceof String str) {
            String result = str;
            for (MaskingPattern pattern : patterns) result = pattern.applyTo(result);
            return result;
        }
        if (obj instanceof LocalDate date) {
            String result = date.format(ISO_LOCAL_DATE);
            for (MaskingPattern pattern : patterns) result = pattern.applyTo(result);
            return result;
        }
        return mask(obj, patterns[0]);
    }
    
    private static String maskObject(Object obj) {
        try {
            VISITED.get().clear();
            return maskObject(obj, VISITED.get());
        } finally {
            VISITED.get().clear();
        }
    }
    
    private static String maskObject(Object obj, Set<Integer> visited) {
        if (obj == null) return "null";
        int identity = identityHashCode(obj);
        if (!visited.add(identity)) {
            return "[circular-ref]";
        }
        try {
            Class<?> clazz = obj.getClass();
            if (!clazz.isAnnotationPresent(SensitiveObject.class)) {
                return safeToString(obj);
            }
            Map<String, Field> sensitiveFields = getSensitiveFields(clazz);
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length == 0) return safeToString(obj);
            StringBuilder result = new StringBuilder(clazz.getSimpleName()).append("{");
            boolean first = true;
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    String maskedValue = maskField(field, value, sensitiveFields, visited);
                    appendField(result, field.getName(), maskedValue, first);
                    first = false;
                } catch (Exception e) {
                    appendField(result, field.getName(), "[access-error]", first);
                    first = false;
                }
            }
            result.append("}");
            return result.toString();
        } finally {
            visited.remove(identity);
        }
    }
    
    private static String maskField(Field field, Object value, Map<String, Field> sensitiveFields, Set<Integer> visited) {
        if (sensitiveFields.containsKey(field.getName())) {
            return maskFieldValue(field, value);
        } else if (shouldRecursivelyMask(value)) {
            return maskObject(value, visited);
        } else {
            return safeToString(value);
        }
    }
    
    private static void appendField(StringBuilder sb, String fieldName, String maskedValue, boolean isFirst) {
        if (!isFirst) sb.append(", ");
        sb.append(fieldName).append("=").append(maskedValue);
    }
    
    private static boolean shouldRecursivelyMask(Object value) {
        if (value == null) return false;
        Class<?> clazz = value.getClass();
        if (isSimpleType(value) || clazz.isEnum() || clazz.isArray()) return false;
        if (Collection.class.isAssignableFrom(clazz)) return false;
        if (Map.class.isAssignableFrom(clazz)) return false;
        if (clazz.getPackageName().startsWith("java.") || clazz.getPackageName().startsWith("javax.")) return false;
        if (Logger.class.isAssignableFrom(clazz)) return false;
        return true;
    }
    
    private static Map<String, Field> getSensitiveFields(Class<?> clazz) {
        return SENSITIVE_FIELD_CACHE.get(clazz, cls -> {
            Map<String, Field> map = new ConcurrentHashMap<>();
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(SensitiveField.class)) {
                    field.setAccessible(true);
                    map.put(field.getName(), field);
                }
            }
            return map;
        });
    }
    
    private static String maskFieldValue(Field field, Object value) {
        if (value == null) return null;
        MaskingPattern[] patterns = getPatterns(field);
        if (patterns.length == 0) {
            if (value instanceof LocalDate date) return date.toString();
            return safeToString(value);
        }
        return maskWithPatterns(value, patterns).toString();
    }
    
    private static MaskingPattern[] getPatterns(Field field) {
        return PATTERN_CACHE.get(field, f -> {
            SensitiveField annotation = f.getAnnotation(SensitiveField.class);
            return annotation != null && annotation.patterns().length > 0
                    ? annotation.patterns()
                    : new MaskingPattern[0];
        });
    }
    
    private static boolean isSimpleType(Object obj) {
        if (obj == null) return true;
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || SIMPLE_TYPES.contains(clazz);
    }
    
    private static String safeToString(Object obj) {
        if (obj == null) return "null";
        try {
            return obj.toString();
        } catch (Exception e) {
            return "[unprintable:" + obj.getClass().getSimpleName() + "]";
        }
    }
}