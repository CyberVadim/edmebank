package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByClientId(UUID clientId);
    List<Notification> findByStatus(Notification.NotificationStatus status);
    List<Notification> findByStatusAndCreatedAtBefore(Notification.NotificationStatus status, Instant createdAt);

    @Query("SELECT n FROM Notification n WHERE " +
            "n.clientConfirmed = false AND " +
            "n.status IN ('DELIVERED', 'SENT') AND " +
            "n.createdAt <= :cutoffDate")
    List<Notification> findUnconfirmedNotifications(
            @Param("cutoffDate") Instant cutoffDate
    );
}

