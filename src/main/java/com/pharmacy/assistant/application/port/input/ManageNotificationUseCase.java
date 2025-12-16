package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreateNotificationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateNotificationRequest;
import com.pharmacy.assistant.application.dto.response.NotificationResponse;
import com.pharmacy.assistant.application.dto.response.NotificationSummaryResponse;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;

import java.util.List;
import java.util.UUID;

/**
 * Use Case Port for Notification Management
 * Defines business operations for notification management
 */
public interface ManageNotificationUseCase {

    // CRUD Operations
    NotificationResponse createNotification(CreateNotificationRequest request);
    NotificationResponse updateNotification(UUID id, UpdateNotificationRequest request);
    NotificationResponse getNotificationById(UUID id);
    List<NotificationResponse> getAllNotifications();
    List<NotificationSummaryResponse> getAllNotificationsSummary();
    void deleteNotification(UUID id);

    // Query Operations
    List<NotificationResponse> getNotificationsByPatientId(UUID patientId);
    List<NotificationResponse> getNotificationsByStatus(NotificationStatus status);
    List<NotificationResponse> getNotificationsByType(NotificationType type);
    List<NotificationResponse> getNotificationsByChannel(NotificationChannel channel);
    List<NotificationResponse> getPendingNotifications();
    List<NotificationResponse> getScheduledNotifications();
    List<NotificationResponse> getSentNotifications();
    List<NotificationResponse> getFailedNotifications();
    List<NotificationResponse> getUnreadNotifications(UUID patientId);

    // Notification Actions
    void sendNotification(UUID id);
    void sendPendingNotifications(); // Batch gönderim için
    void markAsSent(UUID id);
    void markAsDelivered(UUID id);
    void markAsRead(UUID id);
    void markAsFailed(UUID id, String errorMessage);
    void retryFailedNotification(UUID id);

    // Scheduled Notifications
    List<NotificationResponse> getNotificationsDueForSending();
    void processScheduledNotifications();

    // Business Logic - Automatic Notification Creation
    void createMedicationExpiryNotification(UUID medicationId);
    void createPrescriptionExpiryNotification(UUID prescriptionId);
    void createStockLowNotification(UUID stockId);
    void createStockExpiryNotification(UUID stockId);

    // Statistics
    long getTotalNotificationCount();
    long getNotificationCountByPatient(UUID patientId);
    long getNotificationCountByStatus(NotificationStatus status);
    long getNotificationCountByType(NotificationType type);
    long getUnreadCount(UUID patientId);
}