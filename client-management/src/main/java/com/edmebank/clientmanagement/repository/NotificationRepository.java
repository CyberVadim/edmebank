package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByClientId(UUID clientId);
    List<Notification> findByClientIdAndStatusIn(UUID clientId, List<Notification.NotificationStatus> statusList);
    Optional<Notification> findFirstByClientIdAndType(UUID clientId, Notification.NotificationType type);
    List<Notification> findByStatus(Notification.NotificationStatus status);
    List<Notification> findByStatusAndLastAttemptAtBeforeAndAttemptCountLessThan(Notification.NotificationStatus status,
                                                                             Instant lastAttemptBefore,
                                                                             int maxAttemptCount);
}

