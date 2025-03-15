package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.model.notification.Notification;
import com.edmebank.clientmanagement.model.notification.Notification.NotificationStatus;
import com.edmebank.clientmanagement.model.notification.Notification.NotificationType;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void resendUnconfirmedNotifications_ShouldResendIfNotConfirmedAndAttemptsLessThan3() {
        // Arrange
        Notification notification = Notification.builder()
                .id(1L)
                .clientId(UUID.randomUUID())
                .email("murik311088@yandex.ru")
                .message("Reminder")
                .type(NotificationType.PASSPORT_EXPIRY)
                .status(NotificationStatus.SENT)
                .attemptCount(2)
                .clientConfirmed(false)
                .createdAt(Instant.now().minusSeconds(259200)) // 3 дня назад
                .build();

        when(notificationRepository.findByStatusAndCreatedAtBefore(eq(NotificationStatus.SENT), any()))
                .thenReturn(List.of(notification));

        // Act
        notificationService.resendUnconfirmedNotifications();

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        assertEquals(3, notification.getAttemptCount());
    }

    @Test
    void resendUnconfirmedNotifications_ShouldNotResendIfAttemptsReached3() {
        // Arrange
        Notification notification = Notification.builder()
                .id(2L)
                .clientId(UUID.randomUUID())
                .email("murik311088@yandex.ru")
                .message("Reminder")
                .type(NotificationType.PASSPORT_EXPIRY)
                .status(NotificationStatus.SENT)
                .attemptCount(3) // Уже 3 попытки
                .clientConfirmed(false)
                .createdAt(Instant.now().minusSeconds(259200))
                .build();

        when(notificationRepository.findByStatusAndCreatedAtBefore(eq(NotificationStatus.SENT), any()))
                .thenReturn(List.of(notification));

        // Act
        notificationService.resendUnconfirmedNotifications();

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void sendNotification_ShouldSendPendingNotifications() {
        // Arrange
        Notification notification = Notification.builder()
                .id(3L)
                .clientId(UUID.randomUUID())
                .email("murik311088@yandex.ru")
                .message("New update")
                .type(NotificationType.PRODUCT_UPDATE)
                .status(NotificationStatus.PENDING)
                .attemptCount(0)
                .createdAt(Instant.now())
                .build();

        when(notificationRepository.findByStatus(NotificationStatus.PENDING))
                .thenReturn(List.of(notification));

        // Act
        notificationService.sendNotification();

        // Assert
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(1, notification.getAttemptCount());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}

