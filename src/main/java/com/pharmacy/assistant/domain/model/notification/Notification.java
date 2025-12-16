package com.pharmacy.assistant.domain.model.notification;

import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import com.pharmacy.assistant.domain.valueobject.NotificationRecipient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Domain Model
 * Represents notifications sent to patients via SMS/Email
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    private UUID patientId;                    // İlişkili hasta ID'si
    private NotificationType type;             // Bildirim türü (İlaç Hatırlatma, Reçete Yenileme, vb.)
    private NotificationChannel channel;       // Bildirim kanalı (SMS, Email)
    private NotificationStatus status;         // Bildirim durumu
    private NotificationRecipient recipient;   // Alıcı bilgileri (Value Object)

    private String subject;                    // Konu (Email için)
    private String message;                    // Mesaj içeriği
    private String templateName;               // Şablon adı (opsiyonel)

    private LocalDateTime scheduledAt;         // Planlanmış gönderim zamanı
    private LocalDateTime sentAt;              // Gönderildiği zaman
    private LocalDateTime deliveredAt;         // İletildiği zaman
    private LocalDateTime readAt;              // Okunduğu zaman (tracking için)

    private String externalId;                 // Harici servis ID'si (SMS provider vb.)
    private Integer retryCount;                // Tekrar deneme sayısı
    private Integer maxRetries;                // Maksimum tekrar deneme sayısı

    private String errorMessage;               // Hata mesajı (başarısız durumda)
    private String metadata;                   // Ek bilgiler (JSON string)

    /**
     * Checks if notification is pending
     */
    public boolean isPending() {
        return NotificationStatus.PENDING.equals(status);
    }

    /**
     * Checks if notification was sent successfully
     */
    public boolean isSent() {
        return NotificationStatus.SENT.equals(status);
    }

    /**
     * Checks if notification was delivered
     */
    public boolean isDelivered() {
        return NotificationStatus.DELIVERED.equals(status);
    }

    /**
     * Checks if notification failed
     */
    public boolean isFailed() {
        return NotificationStatus.FAILED.equals(status);
    }

    /**
     * Checks if notification can be retried
     */
    public boolean canRetry() {
        return isFailed() &&
                retryCount != null &&
                maxRetries != null &&
                retryCount < maxRetries;
    }

    /**
     * Checks if notification is scheduled for future
     */
    public boolean isScheduled() {
        return scheduledAt != null &&
                LocalDateTime.now().isBefore(scheduledAt);
    }

    /**
     * Checks if notification is ready to send
     */
    public boolean isReadyToSend() {
        return isPending() &&
                (scheduledAt == null || !LocalDateTime.now().isBefore(scheduledAt));
    }

    /**
     * Marks notification as sent
     */
    public void markAsSent(String externalId) {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.externalId = externalId;
    }

    /**
     * Marks notification as delivered
     */
    public void markAsDelivered() {
        if (isSent()) {
            this.status = NotificationStatus.DELIVERED;
            this.deliveredAt = LocalDateTime.now();
        }
    }

    /**
     * Marks notification as read
     */
    public void markAsRead() {
        if (isDelivered()) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Marks notification as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * Increments retry count
     */
    public void incrementRetryCount() {
        if (retryCount == null) {
            retryCount = 0;
        }
        retryCount++;
    }

    /**
     * Resets for retry
     */
    public void resetForRetry() {
        if (canRetry()) {
            this.status = NotificationStatus.PENDING;
            this.errorMessage = null;
            incrementRetryCount();
        }
    }

    /**
     * Cancels the notification
     */
    public void cancel() {
        if (isPending()) {
            this.status = NotificationStatus.CANCELLED;
        }
    }

    /**
     * Validates notification has required fields
     */
    public boolean isValid() {
        return patientId != null &&
                type != null &&
                channel != null &&
                recipient != null &&
                recipient.isValid() &&
                message != null &&
                !message.isEmpty();
    }

    /**
     * Gets time until scheduled send
     */
    public Long getMinutesUntilScheduled() {
        if (scheduledAt == null) {
            return 0L;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(scheduledAt)) {
            return 0L;
        }
        return java.time.Duration.between(now, scheduledAt).toMinutes();
    }

    /**
     * Gets time since sent
     */
    public Long getMinutesSinceSent() {
        if (sentAt == null) {
            return null;
        }
        return java.time.Duration.between(sentAt, LocalDateTime.now()).toMinutes();
    }

    /**
     * Checks if notification requires consent
     */
    public boolean requiresConsent() {
        return NotificationChannel.SMS.equals(channel) ||
                NotificationChannel.EMAIL.equals(channel);
    }
}