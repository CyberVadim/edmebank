package com.edmebank.clientmanagement.controller;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@RestController
@RequestMapping("/send")
public class EmailController {
    private final JavaMailSender mailSender;
    @Value("${mail.sender}")
    private String senderEmail;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Проверяет доступность сайта, доступного только через VPN.
     */
    private boolean isVpnActive() {
        return isSiteAccessible("https://www.rutracker.org");
    }

    /**
     * Проверяет, доступен ли указанный сайт.
     */
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


    @GetMapping
    public String sendEmail() {
        if (isVpnActive()) {
            log.warn("VPN is active. SMTP may be blocked.");
            return "Error: Disable VPN and try again.";
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo("murik311088@yandex.ru");
            helper.setSubject("Simple Subject");
            helper.setText("Simple Text", true);
            mailSender.send(message);
            log.info("Email sent to {}", "murik311088@yandex.ru");
            return "Email sent successfully";
        } catch (Exception e) {
            log.error("Failed to send email to {}", "murik311088@yandex.ru", e);
            return e.getMessage();
        }
    }
}
