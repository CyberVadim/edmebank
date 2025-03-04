package com.edmebank.clientmanagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/send")
public class EmailController {
    private final JavaMailSender mailSender;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @GetMapping
    public String sendEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("edmebank@mail.ru");
            message.setTo("murik311088@yandex.ru");
            message.setSubject("Simple Subject");
            message.setText("Simple Text");
            mailSender.send(message);
            return "Email sent successfully";
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return e.getMessage();
        }
    }
}
