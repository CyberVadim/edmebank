package com.edmebank.clientmanagement.model.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID clientId;
    private String email;
    private String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    public enum NotificationType {
        INFO,
        WARNING,
        ERROR,
        ALERT
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }
}

