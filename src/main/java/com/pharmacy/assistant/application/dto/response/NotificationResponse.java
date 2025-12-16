package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String message;
    private String recipient; // Email veya telefon numarası

    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    private UUID relatedEntityId;
    private String relatedEntityType;

    private Integer priority;
    private Integer retryCount;
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}