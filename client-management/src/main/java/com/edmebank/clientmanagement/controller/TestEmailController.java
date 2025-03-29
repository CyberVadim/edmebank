package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.model.Notification;
import com.edmebank.clientmanagement.service.sending.MailSenderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.edmebank.clientmanagement.model.Notification.NotificationType.PASSPORT_EXPIRY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/send")
public class TestEmailController { // todo вынести в тесты

    private final MailSenderImpl mailSender;

    @GetMapping
    public String sendEmail() {
        Notification notification = Notification.builder()
                .email("murik311088@yandex.ru")
                .type(PASSPORT_EXPIRY)
                .message("Simple message")
                .build();
        mailSender.send(notification);
        log.info("Email sent to {}", "murik311088@yandex.ru");
        return "Email sent successfully";
    }
}
