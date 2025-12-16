package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull(message = "Hasta ID'si boş olamaz")
    private UUID patientId;

    @NotNull(message = "Bildirim türü seçilmelidir")
    private NotificationType type;

    @NotNull(message = "Bildirim kanalı seçilmelidir")
    private NotificationChannel channel;

    @NotBlank(message = "Başlık boş olamaz")
    private String title;

    @NotBlank(message = "Mesaj içeriği boş olamaz")
    private String message;

    private LocalDateTime scheduledFor; // Null ise hemen gönderilir

    private UUID relatedEntityId; // İlgili Medication, Prescription veya Stock ID'si

    private String relatedEntityType; // "MEDICATION", "PRESCRIPTION", "STOCK" gibi

    private Integer priority; // 1-5 arası öncelik (5 en yüksek)
}