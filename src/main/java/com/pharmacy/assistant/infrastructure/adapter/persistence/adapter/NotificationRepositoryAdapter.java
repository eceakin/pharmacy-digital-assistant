package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.NotificationRepository;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.domain.model.notification.Notification;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.NotificationEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = toEntity(notification);
        NotificationEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notification> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Notification> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByPatientId(UUID patientId) {
        return jpaRepository.findUnreadByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findPendingNotifications() {
        return jpaRepository.findPendingNotifications().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findSentNotifications() {
        return jpaRepository.findSentNotifications().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findFailedNotifications() {
        return jpaRepository.findFailedNotifications().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByType(NotificationType type) {
        return jpaRepository.findByType(type).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByChannel(NotificationChannel channel) {
        return jpaRepository.findByChannel(channel).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByPatientIdAndType(UUID patientId, NotificationType type) {
        return jpaRepository.findByPatientIdAndType(patientId, type).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findScheduledNotifications() {
        return jpaRepository.findScheduledNotifications().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findNotificationsDueForSending(LocalDateTime currentTime) {
        return jpaRepository.findNotificationsDueForSending(currentTime).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByScheduledForBetween(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByScheduledForBetween(start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByRelatedEntityId(UUID relatedEntityId) {
        return jpaRepository.findByRelatedEntityId(relatedEntityId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByRelatedEntityIdAndType(UUID relatedEntityId, String relatedEntityType) {
        return jpaRepository.findByRelatedEntityIdAndType(relatedEntityId, relatedEntityType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPatientId(UUID patientId) {
        return jpaRepository.countByPatientId(patientId);
    }

    @Override
    public long countByStatus(NotificationStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countByType(NotificationType type) {
        return jpaRepository.countByType(type);
    }

    @Override
    public long countUnreadByPatientId(UUID patientId) {
        return jpaRepository.countUnreadByPatientId(patientId);
    }

    @Override
    public long countFailedNotifications() {
        return jpaRepository.countFailedNotifications();
    }

    // --- MAPPING METHODS ---

    private NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setPatientId(notification.getPatientId());
        entity.setType(notification.getType());
        entity.setChannel(notification.getChannel());
        entity.setStatus(notification.getStatus());
        entity.setTitle(notification.getTitle());
        entity.setMessage(notification.getMessage());
        entity.setRecipient(notification.getRecipient());
        entity.setScheduledFor(notification.getScheduledFor());
        entity.setSentAt(notification.getSentAt());
        entity.setReadAt(notification.getReadAt());
        entity.setRelatedEntityId(notification.getRelatedEntityId());
        entity.setRelatedEntityType(notification.getRelatedEntityType());
        entity.setPriority(notification.getPriority());
        entity.setRetryCount(notification.getRetryCount());
        entity.setMaxRetries(notification.getMaxRetries());
        entity.setErrorMessage(notification.getErrorMessage());
        entity.setCreatedAt(notification.getCreatedAt());
        entity.setUpdatedAt(notification.getUpdatedAt());
        return entity;
    }

    private Notification toDomain(NotificationEntity entity) {
        Notification notification = new Notification();
        notification.setId(entity.getId());
        notification.setPatientId(entity.getPatientId());
        notification.setType(entity.getType());
        notification.setChannel(entity.getChannel());
        notification.setStatus(entity.getStatus());
        notification.setTitle(entity.getTitle());
        notification.setMessage(entity.getMessage());
        notification.setRecipient(entity.getRecipient());
        notification.setScheduledFor(entity.getScheduledFor());
        notification.setSentAt(entity.getSentAt());
        notification.setReadAt(entity.getReadAt());
        notification.setRelatedEntityId(entity.getRelatedEntityId());
        notification.setRelatedEntityType(entity.getRelatedEntityType());
        notification.setPriority(entity.getPriority());
        notification.setRetryCount(entity.getRetryCount());
        notification.setMaxRetries(entity.getMaxRetries());
        notification.setErrorMessage(entity.getErrorMessage());
        notification.setCreatedAt(entity.getCreatedAt());
        notification.setUpdatedAt(entity.getUpdatedAt());
        return notification;
    }
}