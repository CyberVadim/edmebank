//package com.edmebank.clientmanagement.producer;
//
//import com.edmebank.clientmanagement.dto.NotificationRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NotificationKafkaProducer {
//
//    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
//
//    @Autowired
//    public NotificationKafkaProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendNotificationEvent(NotificationRequest request) {
//        kafkaTemplate.send("notification-events", request);
//    }
//}
