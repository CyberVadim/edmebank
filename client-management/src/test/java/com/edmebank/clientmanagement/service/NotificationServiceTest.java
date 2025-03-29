package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.model.Notification;
import com.edmebank.clientmanagement.model.Notification.NotificationStatus;
import com.edmebank.clientmanagement.model.Notification.NotificationType;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import com.edmebank.clientmanagement.service.sending.MailSenderImpl;
import com.edmebank.clientmanagement.service.sending.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NotificationService.class)
@TestPropertySource(properties = {
        "notification.passport_expire.before_months=3",
        "notification.max_attempt=3",
        "server.port=8081"
})
@MockitoBeans({
        @MockitoBean(types = MailSenderImpl.class),
        @MockitoBean(types = NotificationRepository.class),
        @MockitoBean(types = ClientRepository.class),
        @MockitoBean(types = ExecutorService.class)
})
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    /* @BeforeEach // todo Marchenko нет смысла перед каждым тестом, но @BeforeAll требует static - идеи?
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
                .createdAt(Instant.now().minusSeconds(259200)) // 3 дня назад
                .build();

        when(notificationRepository.findByStatusAndLastAttemptAtBeforeAndAttemptCountLessThan(
                eq(NotificationStatus.SENT), any(), eq(3)))
                .thenReturn(List.of(notification));
        // Act
        notificationService.resendUnconfirmedNotifications();
        // Assert
        // verify(notificationRepository, times(1)).save(any(Notification.class));
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
        // Act
        notificationService.sendNotification();
        // Assert
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(1, notification.getAttemptCount());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

     */
}

