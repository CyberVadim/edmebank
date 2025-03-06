package com.edmebank.clientmanagement.dto;

import com.edmebank.clientmanagement.model.notification.Notification;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationRequest {
    private UUID clientId;
    private String email;
    private String message;
    private Notification.NotificationType type;

    public NotificationRequest(UUID clientId, String email, String message, Notification.NotificationType type) {
        this.clientId = clientId;
        this.email = email;
        this.message = message;
        this.type = type;
    }
}
