package com.edmebank.clientmanagement.service.sending;

import com.edmebank.clientmanagement.exception.VpnActiveException;
import com.edmebank.clientmanagement.model.Notification;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.edmebank.clientmanagement.model.Notification.NotificationType.DEPOSIT_INTEREST;
import static com.edmebank.clientmanagement.model.Notification.NotificationType.PASSPORT_EXPIRY;
import static com.edmebank.clientmanagement.model.Notification.NotificationType.PRODUCT_UPDATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderImpl implements Sender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static Map<Notification.NotificationType, String > typeStringMap = new HashMap<>();
    static {
        typeStringMap.put(PASSPORT_EXPIRY, "Требуется замена паспорта");
        typeStringMap.put(PRODUCT_UPDATE, "Изменение условий обслуживания");
        typeStringMap.put(DEPOSIT_INTEREST, "Начисление процентов по депозиту");
    }

    @Override
    public void send(Notification notification) {
        sendEmail(notification.getEmail(), typeStringMap.get(notification.getType()),
                notification.getMessage(), true);
    }

    public void sendEmail(String to, String subject, String text, boolean isHtml) {
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
}
