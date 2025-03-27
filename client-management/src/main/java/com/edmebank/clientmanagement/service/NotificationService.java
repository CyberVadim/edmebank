package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.exception.VpnActiveException;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.notification.Notification;
import com.edmebank.clientmanagement.model.notification.Notification.NotificationStatus;
import com.edmebank.clientmanagement.model.notification.Notification.NotificationType;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, ClientRepository clientRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.clientRepository = clientRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void processNotifications() {
        log.info("Processing notifications");
        createPassportExpiryNotifications();
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendNotifications() {
        log.info("Sending notifications");
        sendNotification();
        resendUnconfirmedNotifications();
    }

    public void createPassportExpiryNotifications() {
        LocalDate expiryThreshold = LocalDate.now().plusMonths(3).plusDays(1);
        List<Client> clients = clientRepository.findByPassportExpiryDateBefore(expiryThreshold);

        for (Client client : clients) {
            boolean alreadyNotified = notificationRepository.findByClientId(client.getId()).stream()
                    .anyMatch(n -> n.getType() == NotificationType.PASSPORT_EXPIRY);
            if (!alreadyNotified) {
                Notification notification = Notification.builder()
                        .clientId(client.getId())
                        .email(client.getEmail())
                        .message("Ваш паспорт скоро истекает. Пожалуйста, обновите его.")
                        .type(NotificationType.PASSPORT_EXPIRY)
                        .status(NotificationStatus.PENDING)
                        .createdAt(Instant.now())
                        .attemptCount(0)
                        .build();
                notificationRepository.save(notification);
            }
        }
    }

    void resendUnconfirmedNotifications() {
        List<Notification> notifications = notificationRepository.findByStatusAndCreatedAtBefore(
                NotificationStatus.SENT, Instant.now().minus(3, ChronoUnit.DAYS));

        for (Notification notification : notifications) {
            if (!notification.isClientConfirmed() && notification.getAttemptCount() < 3) {
                sendEmail(notification.getEmail(), generateSubject(notification.getType()), notification.getMessage(), true);
                notification.setLastAttemptAt(Instant.now());
                notification.setAttemptCount(notification.getAttemptCount() + 1);
                notificationRepository.save(notification);
            }
        }
    }

    void sendNotification() {
        List<Notification> notifications = notificationRepository.findByStatus(NotificationStatus.PENDING);
        for (Notification notification : notifications) {
            sendEmail(notification.getEmail(), generateSubject(notification.getType()), notification.getMessage(), true);
            notification.setStatus(NotificationStatus.SENT);
            notification.setLastAttemptAt(Instant.now());
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            notificationRepository.save(notification);
        }
    }


    private String generateSubject(NotificationType type) {
        return switch (type) {
            case PASSPORT_EXPIRY -> "Требуется замена паспорта";
            case PRODUCT_UPDATE -> "Изменение условий обслуживания";
            case DEPOSIT_INTEREST -> "Начисление процентов по депозиту";
        };
    }

    @Async
    public void sendEmail(String to, String subject, String text, boolean isHtml) {
        if (isVpnActive()) {
            log.warn("Ошибка: Отключите VPN и попробуйте снова.");
            throw new VpnActiveException();
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtml);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }

    public List<Notification> getNotificationHistory(UUID clientId) {
        return notificationRepository.findByClientId(clientId);
    }

    public void confirmNotification(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setClientConfirmed(true);
            notification.setClientResponseDate(Instant.now());
            notificationRepository.save(notification);
        });
    }

    private boolean isVpnActive() {
        return isSiteAccessible("https://www.rutracker.org");
    }

    private boolean isSiteAccessible(String siteUrl) {
        try {
            URL url = new URL(siteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // 3 секунды
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                log.warn("VPN detected! {} is accessible.", siteUrl);
                return true; // VPN включен, так как сайт открылся
            }
        } catch (IOException e) {
            log.info("VPN is likely off. {} is not accessible.", siteUrl);
        }
        return false;
    }
}





