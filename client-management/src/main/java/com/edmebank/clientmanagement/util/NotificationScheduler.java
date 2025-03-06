package com.edmebank.clientmanagement.util;

import com.edmebank.clientmanagement.dto.notification.NotificationDto;
import com.edmebank.clientmanagement.model.notification.Notification;
import com.edmebank.clientmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Autowired
    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Запуск каждый день в 09:00
    public void sendScheduledNotifications() {
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();
        for (Notification notification : pendingNotifications) {
            notificationService.sendNotification(new NotificationDto(
                    notification.getClientId(),
                    notification.getEmail(),
                    notification.getMessage(),
                    notification.getType()
            ));
        }
    }
}
