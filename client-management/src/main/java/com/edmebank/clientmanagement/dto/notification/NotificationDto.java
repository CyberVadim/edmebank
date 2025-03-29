package com.edmebank.clientmanagement.dto.notification;

import com.edmebank.clientmanagement.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private UUID clientId;
    private String email;
    private String message;
    private Notification.NotificationType type;
    private String status;
    private Instant timestamp;
}
