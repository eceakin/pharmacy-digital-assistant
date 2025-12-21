package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.domain.model.notification.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Notification
 * Defines data access operations for Notification aggregate
 */
public interface NotificationRepository {

    // Basic CRUD
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    long count();
    void deleteAll(List<Notification> notifications);
    // Query by Patient
    List<Notification> findByPatientId(UUID patientId);
    List<Notification> findUnreadByPatientId(UUID patientId);

    // Query by Status
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findPendingNotifications();
    List<Notification> findSentNotifications();
    List<Notification> findFailedNotifications();

    // Query by Type and Channel
    List<Notification> findByType(NotificationType type);
    List<Notification> findByChannel(NotificationChannel channel);
    List<Notification> findByPatientIdAndType(UUID patientId, NotificationType type);

    // Query by Schedule
    List<Notification> findScheduledNotifications();
    List<Notification> findNotificationsDueForSending(LocalDateTime currentTime);
    List<Notification> findByScheduledForBetween(LocalDateTime start, LocalDateTime end);

    // Query by Related Entity
    List<Notification> findByRelatedEntityId(UUID relatedEntityId);
    List<Notification> findByRelatedEntityIdAndType(UUID relatedEntityId, String relatedEntityType);

    // Statistics
    long countByPatientId(UUID patientId);
    long countByStatus(NotificationStatus status);
    long countByType(NotificationType type);
    long countUnreadByPatientId(UUID patientId);
    long countFailedNotifications();
}