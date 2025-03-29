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
    @Value("${spring.mail.test_vpn_url}")
    private String testVpnUrl;

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

    private boolean isVpnActive() {
        try {
            URL url = new URL(testVpnUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // 3 секунды
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                log.warn("VPN detected! {} is accessible.", testVpnUrl);
                return true; // VPN включен, так как сайт открылся
            }
        } catch (IOException e) {
            log.info("VPN is likely off. {} is not accessible.", testVpnUrl);
        }
        return false;
    }
}
