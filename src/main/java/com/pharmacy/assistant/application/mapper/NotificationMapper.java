package com.pharmacy.assistant.application.mapper;

import com.pharmacy.assistant.application.dto.request.CreateNotificationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateNotificationRequest;
import com.pharmacy.assistant.application.dto.response.NotificationResponse;
import com.pharmacy.assistant.application.dto.response.NotificationSummaryResponse;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.model.notification.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setPatientId(request.getPatientId());
        notification.setType(request.getType());
        notification.setChannel(request.getChannel());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setScheduledFor(request.getScheduledFor());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : 3);

        // Status belirleme: Zamanlanmış mı yoksa beklemede mi?
        if (request.getScheduledFor() != null) {
            notification.setStatus(NotificationStatus.SCHEDULED);
        } else {
            notification.setStatus(NotificationStatus.PENDING);
        }

        notification.setRetryCount(0);

        return notification;
    }

    public void updateDomainFromRequest(Notification notification, UpdateNotificationRequest request) {
        if (request.getChannel() != null) {
            notification.setChannel(request.getChannel());
        }
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());

        if (request.getScheduledFor() != null) {
            notification.setScheduledFor(request.getScheduledFor());
            // Eğer status PENDING ise, SCHEDULED'a çek
            if (NotificationStatus.PENDING.equals(notification.getStatus())) {
                notification.setStatus(NotificationStatus.SCHEDULED);
            }
        }

        if (request.getPriority() != null) {
            notification.setPriority(request.getPriority());
        }
    }

    public NotificationResponse toResponse(Notification notification, String patientName, String recipient) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .patientId(notification.getPatientId())
                .patientName(patientName)
                .type(notification.getType())
                .channel(notification.getChannel())
                .status(notification.getStatus())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .recipient(recipient)
                .scheduledFor(notification.getScheduledFor())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .priority(notification.getPriority())
                .retryCount(notification.getRetryCount())
                .errorMessage(notification.getErrorMessage())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    public NotificationSummaryResponse toSummaryResponse(Notification notification, String patientName) {
        return NotificationSummaryResponse.builder()
                .id(notification.getId())
                .patientName(patientName)
                .type(notification.getType())
                .channel(notification.getChannel())
                .status(notification.getStatus())
               // .title(notification.getTitle())
                .scheduledFor(notification.getScheduledFor())
                .sentAt(notification.getSentAt())
                .priority(notification.getPriority())
                .build();
    }
}