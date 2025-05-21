package ru.edmebank.clients.app.impl.service.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.edmebank.clients.app.api.repository.CreditHistoryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class DelinquencyDaysScheduler {

    private final CreditHistoryRepository creditHistoryRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void recalculationDelinquencyDays() {
        log.info("пересчёт просрочки");
        creditHistoryRepository.updateDelinquencyDays();
    }
}
