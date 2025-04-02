package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.Notification;
import com.edmebank.clientmanagement.model.Notification.NotificationStatus;
import com.edmebank.clientmanagement.model.Notification.NotificationType;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import com.edmebank.clientmanagement.service.sending.Sender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.edmebank.clientmanagement.model.Notification.NotificationStatus.DELIVERED;
import static com.edmebank.clientmanagement.model.Notification.NotificationStatus.DISABLE;
import static com.edmebank.clientmanagement.model.Notification.NotificationStatus.PENDING;
import static com.edmebank.clientmanagement.model.Notification.NotificationStatus.SENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final List<Sender> senders;
    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;
    private final ExecutorService executorService;

    @Value("${notification.passport_expire.before_months}")
    private int notificationBeforeMonths;
    @Getter
    @Value("${notification.max_attempt}")
    private int maxAttempt;
    @Value("${server.port}")
    private int serverPort;

    @Transactional
    public void createPassportExpiryNotifications() {
        LocalDate expiryThreshold = LocalDate.now().plusMonths(notificationBeforeMonths);
        List<Client> clients = clientRepository.findByPassportExpiryDateBefore(expiryThreshold);

        for (Client client : clients) {
            Optional<Notification> optionalNotification = notificationRepository.findFirstByClientIdAndType(client.getId(), NotificationType.PASSPORT_EXPIRY);
            if (optionalNotification.isEmpty()) {

                Notification notification = Notification.builder()
                        .clientId(client.getId())
                        .email(client.getEmail())
                        .message("NotNull")  // todo Marchenko есть другие идеи?
                        .type(NotificationType.PASSPORT_EXPIRY)
                        .status(PENDING)
                        .createdAt(Instant.now())
                        .attemptCount(0)
                        .build();
                notification = notificationRepository.save(notification);

                String message = String.format("""
                Ваш паспорт истекает %s. Пожалуйста, обновите его.
                Для подтверждения уведомления перейдите по ссылке:
                http://localhost:%d/api/v1/notifications/confirm?notificationId=%d.
                Чтобы отписаться от уведомлений пройдите по ссылке:
                http://localhost:%d/api/v1/clients/%s/disableNotification""",
                        client.getPassportExpiryDate(), serverPort, notification.getId(), serverPort, client.getId());
                //todo Marchenko идеи как применить один параметр дважды? что насчёт localhost - домена же нет?

                notification.setMessage(message);
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional
    public void sendNotification() {
        List<Notification> notifications = notificationRepository.findByStatus(PENDING);
        for (Notification notification : notifications) {
            senders.forEach(sender -> executorService.submit(() -> sender.send(notification)));
            notification.setStatus(SENT);
            notification.setLastAttemptAt(Instant.now());
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void resendUnconfirmedNotifications() {
        Instant lastAttemptBefore = Instant.now().minus(3, ChronoUnit.DAYS);
        List<Notification> notifications =
                notificationRepository.findByStatusAndLastAttemptAtBeforeAndAttemptCountLessThan(
                SENT, lastAttemptBefore, maxAttempt);

        for (Notification notification : notifications) {
            senders.forEach(sender -> executorService.submit(() -> sender.send(notification)));
            notification.setLastAttemptAt(Instant.now());
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void confirmNotification(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(DELIVERED);
            notification.setClientResponseDate(Instant.now());
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void disableNotification(UUID clientId) {
        List<Notification> notifications =
                notificationRepository.findByClientIdAndStatusIn(clientId, List.of(PENDING, SENT));
        for (Notification notification: notifications) {
            notification.setStatus(DISABLE);
        }
        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationHistory(UUID clientId) {
        return notificationRepository.findByClientId(clientId);
    }
}