package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.Notification;
import com.edmebank.clientmanagement.model.Notification.NotificationStatus;
import com.edmebank.clientmanagement.model.Notification.NotificationType;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class NotificationServiceTest {
    // todo gennady нужен запуск тестов без подключения к БД - предложения?

    @MockitoBean
    NotificationRepository notificationRepository;
    @MockitoBean
    ClientRepository clientRepository;
    @Autowired
    private NotificationService notificationService;

    @Test
    void createPassportExpiryNotificationsTest() {
        UUID uuid = UUID.fromString("10000000-0000-0000-0000-000000000201");
        Client client = Client.builder()
                .id(uuid)
                .email("Relect@bk.ru")
                .passportExpiryDate(LocalDate.of(2025, 4, 2))
                .build();
        when(clientRepository.findByPassportExpiryDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(client));
        when(notificationRepository.findFirstByClientIdAndType(uuid, NotificationType.PASSPORT_EXPIRY))
                .thenReturn(Optional.empty());
        AtomicReference<Notification> result = new AtomicReference<>();
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    notification.setId(1L);
                    result.set(notification);
                    return notification;
                }).thenAnswer(invocation -> invocation.getArgument(0));
        // Action
        notificationService.createPassportExpiryNotifications();
        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
        String expectedMessage = getMessage();
        assertEquals(expectedMessage, result.get().getMessage());
        assertEquals(NotificationStatus.PENDING, result.get().getStatus());
        assertEquals(NotificationType.PASSPORT_EXPIRY, result.get().getType());
    }

    private String getMessage() {
        return String.format("""
                Ваш паспорт истекает 2025-04-02. Пожалуйста, обновите его.
                Для подтверждения уведомления перейдите по ссылке:
                http://localhost:%S/api/v1/notifications/confirm?notificationId=1.
                Чтобы отписаться от уведомлений пройдите по ссылке:
                http://localhost:%s/api/v1/clients/10000000-0000-0000-0000-000000000201/disableNotification""",
                notificationService.getServerPort(), notificationService.getServerPort());
    }

    @Test
    void resendUnconfirmedNotifications_ShouldResendIfNotConfirmedAndAttemptsLessThan3() {
        // Arrange
        Notification notification = Notification.builder()
                .id(1L)
                .clientId(UUID.randomUUID())
                .email("Relect@bk.ru")
                .message("Reminder")
                .type(NotificationType.PASSPORT_EXPIRY)
                .status(NotificationStatus.SENT)
                .attemptCount(2)
                .createdAt(Instant.now().minusSeconds(259200)) // 3 дня назад
                .build();

        when(notificationRepository.findByStatusAndLastAttemptAtBeforeAndAttemptCountLessThan(
                eq(NotificationStatus.SENT), any(), eq(notificationService.getMaxAttempt())))
                .thenReturn(List.of(notification));
        // Action
        notificationService.resendUnconfirmedNotifications();
        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        assertEquals(3, notification.getAttemptCount());
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
        // Action
        notificationService.sendNotification();
        // Assert
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(1, notification.getAttemptCount());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
