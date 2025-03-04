package com.edmebank.clientmanagement.model.notification;

public enum NotificationStatus {
    PENDING,  // Уведомление ожидает отправки
    SENT,     // Уведомление было успешно отправлено
    FAILED    // Уведомление не удалось отправить
}
