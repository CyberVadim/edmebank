package ru.edmebank.clients.app.impl.service.generatedDto;

import com.github.curiousoddman.rgxgen.RgxGen;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.springframework.stereotype.Service;
import ru.edmebank.clients.app.api.service.GeneratedDtoService;
import ru.edmebank.clients.domain.annotation.MinAge;
import ru.edmebank.clients.fw.exception.DtoGenerationException;
import ru.edmebank.contracts.dto.ClientUiDto;
import ru.edmebank.contracts.enums.ContactType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Реализация сервиса {@link GeneratedDtoService}, предназначенного для генерации mock-объектов DTO или сущностей
 * на основе простого имени класса (если оно входит в список разрешённых пакетов).
 */
@Slf4j
@Service
public class GeneratedDtoServiceImpl implements GeneratedDtoService {

    // Список разрешённых пакетов, в которых ищем классы по имени
    private static final List<String> ALLOWED_PACKAGES = List.of(
            "ru.edmebank.contracts.dto."
    );
    private static final int MAX_AGE = 150;
    private static final String EXCEPTION_FIELD = "при обработке поля: ";
    private static final String EXCEPTION_CLASS = "класс не найден по имени: ";
    private static final int LENGTH_PASSWORD = 8;

    @Override
    public Object generatedDto(String classType) {
        // Определяем полное имя класса по короткому имени (например "Client") и загружаем.
        Class<?> clazz = resolveClassName(classType);

        // Включение обработки @Size, @Min, @Max и других аннотаций
        Settings settings = Settings.create()
                .set(Keys.BEAN_VALIDATION_ENABLED, true);

        Object dto = Instancio.of(clazz)
                .withSettings(settings)
                .create();

        // Создаём Set для отслеживания уже обработанных объектов
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        // Обрабатываем поля вручную (Instancio их пока не учитывает)
        return postProcessPatterns(dto, visited);
    }

    /**
     * Метод генерирует значение по регулярке, подставляя эти значения в объект поля с @Pattern, @MinAge,
     * password, Contact и вложенные DTO.
     */
    private Object postProcessPatterns(Object dto, Set<Object> visited) {
        if (dto == null || visited.contains(dto)) return dto;
        visited.add(dto);

        Class<?> clazz = dto.getClass();
        log.debug("clazz = " + clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(dto);
                log.debug("Имя текущего поля = {}", field.getName());
                log.debug("Значение текущего поля = {}", value);
                log.debug("Тип текущего поля = {} \n", field.getType());

                if (processPatternField(field, dto)) continue;
                if (processMinAgeField(field, dto)) continue;
                if (processCollectionField(field, dto, value, visited)) continue;
                if (processArrayField(value, visited)) continue;
                if (processContactField(clazz, dto)) continue;
                if (processPasswordField(field, dto)) continue;
                processNestedDto(field, dto, value, visited);

            } catch (Exception e) {
                log.error(field.getName());
                throw new DtoGenerationException(EXCEPTION_FIELD + field.getName());
            }
        }

        return dto;
    }

    // 1. Обработка @Pattern для строк - генерация
    private boolean processPatternField(Field field, Object dto) throws IllegalAccessException {
        if (field.isAnnotationPresent(Pattern.class) && field.getType().equals(String.class)) {
            Pattern pattern = field.getAnnotation(Pattern.class);
            RgxGen rgxGen = RgxGen.parse(pattern.regexp());
            String generatedValue = rgxGen.generate();
            field.set(dto, generatedValue);

            return true;
        }
        return false;
    }

    // 2. Обработка @MinAge для LocalDate - генерация
    private boolean processMinAgeField(Field field, Object dto) throws IllegalAccessException {
        if (field.isAnnotationPresent(MinAge.class) && field.getType().equals(LocalDate.class)) {
            MinAge minAge = field.getAnnotation(MinAge.class);
            int age = minAge.value();
            LocalDate youngestAllowed = LocalDate.now().minusYears(age);
            LocalDate oldestAllowed = LocalDate.now().minusYears(MAX_AGE);
            // Генерация даты от 14 до 150 + 14 лет назад
            LocalDate birthDate = generateRandomDateBetween(oldestAllowed, youngestAllowed);
            field.set(dto, birthDate);

            return true;
        }
        return false;
    }

    // 3. Обработка коллекций (List, Set и т.п.)
    private boolean processCollectionField(Field field, Object dto, Object value, Set<Object> visited) throws IllegalAccessException {
        if (value instanceof Collection<?> collection) {
            Collection<Object> newCollection = new ArrayList<>();
            for (Object item : collection) {
                newCollection.add(postProcessPatterns(item, visited));
            }
            field.set(dto, newCollection);

            return true;
        }
        return false;
    }

    // 4. Обработка массивов
    private boolean processArrayField(Object value, Set<Object> visited) {
        if (value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                Object processed = postProcessPatterns(element, visited);
                Array.set(value, i, processed);
            }

            return true;
        }
        return false;
    }

    // 5. Обработка типа ClientUiDto.Contact
    private boolean processContactField(Class<?> clazz, Object dto) {
        if (clazz.equals(ClientUiDto.Contact.class)) {
            ClientUiDto.Contact contact = (ClientUiDto.Contact) dto;
            ContactType type = contact.getType();
            if (type != null && type.pattern != null) {
                java.util.regex.Pattern regex = type.pattern;
                RgxGen rgxGen = RgxGen.parse(regex.pattern());
                String generated = rgxGen.generate();
                contact.setValue(generated);
                log.debug("Сгенерирован {}: {}", type.name(), generated);
            }
            return true;
        }
        return false;
    }

    // 6. Обработка поля "password" - генерация
    private boolean processPasswordField(Field field, Object dto) throws IllegalAccessException {
        if (field.getName().toLowerCase().contains("password") && field.getType().equals(String.class)) {
            String password = PasswordGenerator.generateValidPassword(LENGTH_PASSWORD);
            log.debug("Сгенерирован пароль: {}", password);
            field.set(dto, password);

            return true;
        }
        return false;
    }

    // 7. Обработка обычных вложенных DTO
    private void processNestedDto(Field field, Object dto, Object value, Set<Object> visited) throws IllegalAccessException {
        if (value != null && !isSimpleObject(value.getClass())) {
            Object nested = postProcessPatterns(value, visited);
            field.set(dto, nested);
        }
    }

    // Проверят, является ли входящий тип типом ...
    private boolean isSimpleObject(Class<?> type) {
        return (type.isPrimitive()
                || type.isEnum()
                || String.class.equals(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class.equals(type)
                || Character.class.equals(type)
                || Date.class.isAssignableFrom(type)
                || type.getName().startsWith("java.")
                || type.isArray()
                || Collection.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type));
    }

    // Метод генерации случайной даты между двумя датами
    private LocalDate generateRandomDateBetween(LocalDate minDate, LocalDate maxDate) {
        long days = ChronoUnit.DAYS.between(minDate, maxDate);
        long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
        return minDate.plusDays(randomDays);
    }

    /**
     * Преобразует короткое имя класса в полное имя, проверяя каждый из разрешённых пакетов.
     * Если ни один из пакетов не содержит такого класса — бросает исключение.
     */
    private Class<?> resolveClassName(String classType) {
        for (String basePackage : ALLOWED_PACKAGES) {
            String candidate = basePackage + classType;

            try {
                // Если такой класс существует — возвращаем его
                return Class.forName(candidate);
            } catch (ClassNotFoundException ignored) {
            }
        }

        // Если класс не найден ни в одном пакете — бросаем исключение
        throw new DtoGenerationException(EXCEPTION_CLASS + classType);
    }
}
