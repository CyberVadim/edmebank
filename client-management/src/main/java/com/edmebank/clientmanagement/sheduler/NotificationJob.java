package com.edmebank.clientmanagement.sheduler;

import com.edmebank.clientmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationJob {

    private final NotificationService service;

    @Scheduled(cron = "0 0 8 * * ?")
    public void processNotifications() {
        log.info("Creating notifications");
        service.createPassportExpiryNotifications();
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendNotifications() {
        log.info("Sending notifications");
        service.sendNotification();
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void resendNotifications() {
        log.info("Resending notifications");
        service.resendUnconfirmedNotifications();
    }
}
