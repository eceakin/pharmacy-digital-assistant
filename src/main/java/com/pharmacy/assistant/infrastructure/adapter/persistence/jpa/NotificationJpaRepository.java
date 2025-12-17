package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    // Query by Patient
    List<NotificationEntity> findByPatientId(UUID patientId);

    @Query("SELECT n FROM NotificationEntity n WHERE n.patientId = :patientId " +
            "AND n.status NOT IN ('READ', 'CANCELLED')")
    List<NotificationEntity> findUnreadByPatientId(@Param("patientId") UUID patientId);

    // Query by Status
    List<NotificationEntity> findByStatus(NotificationStatus status);

    @Query("SELECT n FROM NotificationEntity n WHERE n.status = 'PENDING'")
    List<NotificationEntity> findPendingNotifications();

    @Query("SELECT n FROM NotificationEntity n WHERE n.status = 'SCHEDULED'")
    List<NotificationEntity> findScheduledNotifications();

    @Query("SELECT n FROM NotificationEntity n WHERE n.status = 'SENT'")
    List<NotificationEntity> findSentNotifications();

    @Query("SELECT n FROM NotificationEntity n WHERE n.status = 'FAILED'")
    List<NotificationEntity> findFailedNotifications();

    // Query by Type and Channel
    List<NotificationEntity> findByType(NotificationType type);

    List<NotificationEntity> findByChannel(NotificationChannel channel);

    List<NotificationEntity> findByPatientIdAndType(UUID patientId, NotificationType type);

    // Query by Schedule
    @Query("SELECT n FROM NotificationEntity n WHERE " +
            "n.status IN ('PENDING', 'SCHEDULED') " +
            "AND (n.scheduledFor IS NULL OR n.scheduledFor <= :currentTime)")
    List<NotificationEntity> findNotificationsDueForSending(@Param("currentTime") LocalDateTime currentTime);

    List<NotificationEntity> findByScheduledForBetween(LocalDateTime start, LocalDateTime end);

    // Query by Related Entity
    List<NotificationEntity> findByRelatedEntityId(UUID relatedEntityId);

    @Query("SELECT n FROM NotificationEntity n WHERE " +
            "n.relatedEntityId = :relatedEntityId " +
            "AND n.relatedEntityType = :relatedEntityType")
    List<NotificationEntity> findByRelatedEntityIdAndType(
            @Param("relatedEntityId") UUID relatedEntityId,
            @Param("relatedEntityType") String relatedEntityType);

    // Statistics
    long countByPatientId(UUID patientId);

    long countByStatus(NotificationStatus status);

    long countByType(NotificationType type);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE " +
            "n.patientId = :patientId " +
            "AND n.status NOT IN ('READ', 'CANCELLED')")
    long countUnreadByPatientId(@Param("patientId") UUID patientId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.status = 'FAILED'")
    long countFailedNotifications();
}