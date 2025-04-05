package com.edmebank.clientmanagement.service.sending;

import com.edmebank.clientmanagement.model.Notification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.edmebank.clientmanagement.model.Notification.NotificationType.PASSPORT_EXPIRY;

@Disabled("Ручной тест для отправки на email, который вы укажете")
// для запуска закоментируйте @Disabled, проверте работу и раскомментируйте @Disable обратно.
@SpringBootTest
public class EmailSenderImplTest {

    private String emailForSend = "edmebank@mail.ru";
    @Autowired
    private MailSenderImpl mailSender;
    @Test
    public void shouldSendEmailNotification() {
        Notification notification = Notification.builder()
                .email(emailForSend)
                .type(PASSPORT_EXPIRY)
                .message("Simple message")
                .build();
        mailSender.sendNotification(notification);
    }
}
