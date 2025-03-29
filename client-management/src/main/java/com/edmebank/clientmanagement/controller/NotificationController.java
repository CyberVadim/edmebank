package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.model.Notification;
import com.edmebank.clientmanagement.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

//    @PostMapping("/send")
//    public ResponseEntity<String> sendNotification(@RequestBody NotificationDto request) {
//        notificationService.sendNotification(request);
//        return ResponseEntity.ok("Notification sent successfully");
//    }

    @GetMapping("/history/{clientId}")
    public ResponseEntity<List<Notification>> getNotificationHistory(@PathVariable UUID clientId) {
        return ResponseEntity.ok(notificationService.getNotificationHistory(clientId));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmNotification(@RequestParam Long notificationId) {
        notificationService.confirmNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}

