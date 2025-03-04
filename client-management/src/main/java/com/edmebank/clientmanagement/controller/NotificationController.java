package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.NotificationRequest;
import com.edmebank.clientmanagement.dto.notification.NotificationDto;
import com.edmebank.clientmanagement.model.notification.Notification;
import com.edmebank.clientmanagement.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDto request) {
        notificationService.sendNotification(request);
        return ResponseEntity.ok("Notification sent successfully");
    }

    @GetMapping("/history/{clientId}")
    public ResponseEntity<List<Notification>> getNotificationHistory(@PathVariable UUID clientId) {
        return ResponseEntity.ok(notificationService.getNotificationHistory(clientId));
    }
}

