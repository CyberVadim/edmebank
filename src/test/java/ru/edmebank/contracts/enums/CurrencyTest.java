package ru.edmebank.contracts.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class CurrencyTest {
    @ParameterizedTest
    @MethodSource("provideCurrencyData")
    public void testCurrencyMethods(String value, Currency expectedCurrency, String methodName) throws Exception {
        Method method = Currency.class.getMethod(methodName, String.class);

        Currency result = (Currency) method.invoke(null, value);

        assertEquals(expectedCurrency, result);
    }

    static Stream<Object[]> provideCurrencyData() {
        return Stream.of(
                new Object[]{"рубль", Currency.RUB, "fromWholeText"},
                new Object[]{"доллар", Currency.USD, "fromWholeText"},

                new Object[]{"руб.", Currency.RUB, "fromWholeShort"},
                new Object[]{"долл.", Currency.USD, "fromWholeShort"},

                new Object[]{"коп.", Currency.RUB, "fromFractionalShort"},
                new Object[]{"¢", Currency.USD, "fromFractionalShort"},

                new Object[]{"копейка", Currency.RUB, "fromFractionalText"},
                new Object[]{"цент", Currency.USD, "fromFractionalText"}
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCurrencyData")
    public void testCurrencyNotFound(String value, String methodName) throws Exception {
        Method method = Currency.class.getMethod(methodName, String.class);

        Exception exception = assertThrows(Exception.class, () -> method.invoke(null, value));

        if (exception instanceof InvocationTargetException) {
            Throwable cause = exception.getCause();
            assertInstanceOf(IllegalArgumentException.class, cause, "Ожидалось исключение IllegalArgumentException, но было: " + cause.getClass());
        } else {
            fail("Ожидалось InvocationTargetException, но было: " + exception.getClass());
        }
    }

    static Stream<Object[]> provideInvalidCurrencyData() {
        return Stream.of(
                new Object[]{"евро", "fromWholeText"},
                new Object[]{"€", "fromWholeShort"},
                new Object[]{"£", "fromFractionalShort"},
                new Object[]{"пенни", "fromFractionalText"}
        );
    }
}