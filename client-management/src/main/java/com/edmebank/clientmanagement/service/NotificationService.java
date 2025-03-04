package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.notification.NotificationDto;
import com.edmebank.clientmanagement.model.notification.Notification;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public void sendNotification(NotificationDto request) {
        Notification notification = new Notification();
        notification.setClientId(request.getClientId()); // если clientId — UUID
        notification.setEmail(request.getEmail());
        notification.setMessage(request.getMessage());
        notification.setType(Notification.NotificationType.valueOf(request.getType()));
        notification.setStatus(Notification.NotificationStatus.PENDING); // Устанавливаем статус по умолчанию
        notification.setTimestamp(Instant.now());

        notificationRepository.save(notification);
        emailService.sendEmail(request.getEmail(), "Тестовое уведомление", request.getMessage());
    }

    public List<Notification> getNotificationHistory(UUID clientId) {
        return notificationRepository.findByClientId(clientId);
    }

    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatus("PENDING");
    }
}

