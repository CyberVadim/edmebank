//package com.edmebank.clientmanagement.listener;
//
//import com.edmebank.clientmanagement.dto.NotificationRequest;
//import com.edmebank.clientmanagement.service.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NotificationKafkaListener {
//
//    private final NotificationService notificationService;
//
//    @Autowired
//    public NotificationKafkaListener(NotificationService notificationService) {
//        this.notificationService = notificationService;
//    }
//
//    @KafkaListener(topics = "notification-events", groupId = "notification-group")
//    public void listen(NotificationRequest request) {
//        notificationService.sendNotification(request);
//    }
//}
