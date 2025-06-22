package ru.edmebank.accounts.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    // Максимальное количество запросов в минуту
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    // Хранилище для подсчета запросов
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    public boolean tryAcquire() {
        String key = getCurrentMinuteKey();
        RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());

        synchronized (counter) {
            if (counter.getCount() < MAX_REQUESTS_PER_MINUTE) {
                counter.increment();
                // Очистка старых ключей
                cleanupOldEntries();
                return true;
            }
            return false;
        }
    }

    private String getCurrentMinuteKey() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth()
               + "-" + now.getHour() + "-" + now.getMinute();
    }

    private void cleanupOldEntries() {
        String currentKey = getCurrentMinuteKey();
        requestCounters.keySet().removeIf(key -> !key.equals(currentKey));
    }

    @Getter
    private static class RequestCounter {
        private int count = 0;

        public void increment() {
            count++;
        }
    }
}