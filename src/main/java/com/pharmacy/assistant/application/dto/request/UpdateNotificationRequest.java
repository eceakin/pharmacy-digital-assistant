package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationRequest {

    private NotificationChannel channel;

    @NotBlank(message = "Başlık boş olamaz")
    private String title;

    @NotBlank(message = "Mesaj içeriği boş olamaz")
    private String message;

    private LocalDateTime scheduledFor;

    private Integer priority;
}