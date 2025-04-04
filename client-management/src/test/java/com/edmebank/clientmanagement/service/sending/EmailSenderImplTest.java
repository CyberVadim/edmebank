package com.edmebank.clientmanagement.service.sending;

import com.edmebank.clientmanagement.model.Notification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.edmebank.clientmanagement.model.Notification.NotificationType.PASSPORT_EXPIRY;

@Disabled("ручной тест для отправки на email который вы укажите")
// для запуска закоментируйте @Disabled, проверте работу и раскомментируйте @Disable обратно.
@SpringBootTest
public class EmailSenderImplTest {

    @Autowired
    private MailSenderImpl mailSender;
    @Test
    public void sendEmail() {
        Notification notification = Notification.builder()
                .email("edmebank@mail.ru")
                .type(PASSPORT_EXPIRY)
                .message("Simple message")
                .build();
        mailSender.sendNotification(notification);
    }
}
