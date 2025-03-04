package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByClientId(UUID clientId);
    List<Notification> findByStatus(String status);
}

