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
public class NotificationSummaryResponse {
    private UUID id;
    private String patientName;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private Integer priority;
}