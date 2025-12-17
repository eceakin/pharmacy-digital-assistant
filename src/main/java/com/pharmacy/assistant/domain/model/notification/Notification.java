package com.pharmacy.assistant.domain.model.notification;

import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
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

    private UUID patientId;                    // İlişkili hasta ID'si (null olabilir - sistem bildirimleri için)
    private NotificationType type;             // Bildirim türü
    private NotificationChannel channel;       // Bildirim kanalı
    private NotificationStatus status;         // Bildirim durumu

    private String title;                      // Başlık
    private String message;                    // Mesaj içeriği
    private String recipient;                  // Alıcı bilgisi (telefon/email)

    private LocalDateTime scheduledFor;        // Planlanmış gönderim zamanı
    private LocalDateTime sentAt;              // Gönderildiği zaman
    private LocalDateTime readAt;              // Okunduğu zaman

    private UUID relatedEntityId;              // İlişkili entity ID'si
    private String relatedEntityType;          // İlişkili entity tipi

    private Integer priority;                  // Öncelik (1-5)
    private Integer retryCount;                // Tekrar deneme sayısı
    private Integer maxRetries;                // Maksimum tekrar deneme sayısı (default: 3)

    private String errorMessage;               // Hata mesajı

    /**
     * Checks if notification is pending
     */
    public boolean isPending() {
        return NotificationStatus.PENDING.equals(status);
    }

    /**
     * Checks if notification is scheduled
     */
    public boolean isScheduled() {
        return NotificationStatus.SCHEDULED.equals(status);
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
     * Checks if notification was read
     */
    public boolean isRead() {
        return NotificationStatus.READ.equals(status);
    }

    /**
     * Checks if notification failed
     */
    public boolean isFailed() {
        return NotificationStatus.FAILED.equals(status);
    }

    /**
     * Checks if notification can be sent
     */
    public boolean canBeSent() {
        return (isPending() || isScheduled()) && !isFailed();
    }

    /**
     * Checks if notification can be retried
     */
    public boolean canBeRetried() {
        if (maxRetries == null) {
            maxRetries = 3; // Default max retries
        }
        return isFailed() &&
                retryCount != null &&
                retryCount < maxRetries;
    }

    /**
     * Checks if notification is due for sending
     */
    public boolean isDueForSending() {
        if (scheduledFor == null) {
            return isPending();
        }
        return (isPending() || isScheduled()) &&
                LocalDateTime.now().isAfter(scheduledFor);
    }

    /**
     * Marks notification as sent
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    /**
     * Marks notification as delivered
     */
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
    }

    /**
     * Marks notification as read
     */
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Marks notification as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * Retry failed notification
     */
    public void retry() {
        if (canBeRetried()) {
            this.status = NotificationStatus.PENDING;
            this.errorMessage = null;
            this.retryCount++;
        }
    }

    /**
     * Cancel notification
     */
    public void cancel() {
        this.status = NotificationStatus.CANCELLED;
    }

    /**
     * Validates notification
     */
    public boolean isValid() {
        return type != null &&
                channel != null &&
                message != null &&
                !message.isEmpty();
    }
}