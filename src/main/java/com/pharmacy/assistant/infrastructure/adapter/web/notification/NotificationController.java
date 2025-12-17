package com.pharmacy.assistant.infrastructure.adapter.web.notification;

import com.pharmacy.assistant.application.dto.request.CreateNotificationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateNotificationRequest;
import com.pharmacy.assistant.application.dto.response.NotificationResponse;
import com.pharmacy.assistant.application.dto.response.NotificationSummaryResponse;
import com.pharmacy.assistant.application.port.input.ManageNotificationUseCase;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ManageNotificationUseCase manageNotificationUseCase;

    // --- CRUD OPERATIONS ---

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {

        log.info("REST: Creating notification for patient: {}", request.getPatientId());
        NotificationResponse response = manageNotificationUseCase.createNotification(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bildirim başarıyla oluşturuldu", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> updateNotification(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNotificationRequest request) {

        log.info("REST: Updating notification with ID: {}", id);
        NotificationResponse response = manageNotificationUseCase.updateNotification(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim başarıyla güncellendi", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable UUID id) {
        log.info("REST: Fetching notification with ID: {}", id);
        NotificationResponse response = manageNotificationUseCase.getNotificationById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim detayları getirildi", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable UUID id) {
        log.info("REST: Deleting notification with ID: {}", id);
        manageNotificationUseCase.deleteNotification(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim başarıyla silindi", null)
        );
    }

    // --- LISTING AND QUERY ---

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        log.info("REST: Fetching all notifications");
        List<NotificationResponse> responses = manageNotificationUseCase.getAllNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Tüm bildirimler getirildi", responses)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<NotificationSummaryResponse>>> getAllNotificationsSummary() {
        log.info("REST: Fetching notification summaries");
        List<NotificationSummaryResponse> responses = manageNotificationUseCase.getAllNotificationsSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim özet listesi getirildi", responses)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByPatient(
            @PathVariable UUID patientId) {

        log.info("REST: Fetching notifications for patient: {}", patientId);
        List<NotificationResponse> responses = manageNotificationUseCase.getNotificationsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait bildirimler getirildi", responses)
        );
    }

    @GetMapping("/patient/{patientId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @PathVariable UUID patientId) {

        log.info("REST: Fetching unread notifications for patient: {}", patientId);
        List<NotificationResponse> responses = manageNotificationUseCase.getUnreadNotifications(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Okunmamış bildirimler getirildi", responses)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByStatus(
            @PathVariable NotificationStatus status) {

        log.info("REST: Fetching notifications with status: {}", status);
        List<NotificationResponse> responses = manageNotificationUseCase.getNotificationsByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success("Duruma göre bildirimler getirildi", responses)
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByType(
            @PathVariable NotificationType type) {

        log.info("REST: Fetching notifications with type: {}", type);
        List<NotificationResponse> responses = manageNotificationUseCase.getNotificationsByType(type);

        return ResponseEntity.ok(
                ApiResponse.success("Türe göre bildirimler getirildi", responses)
        );
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByChannel(
            @PathVariable NotificationChannel channel) {

        log.info("REST: Fetching notifications with channel: {}", channel);
        List<NotificationResponse> responses = manageNotificationUseCase.getNotificationsByChannel(channel);

        return ResponseEntity.ok(
                ApiResponse.success("Kanala göre bildirimler getirildi", responses)
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getPendingNotifications() {
        log.info("REST: Fetching pending notifications");
        List<NotificationResponse> responses = manageNotificationUseCase.getPendingNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Bekleyen bildirimler getirildi", responses)
        );
    }

    @GetMapping("/scheduled")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getScheduledNotifications() {
        log.info("REST: Fetching scheduled notifications");
        List<NotificationResponse> responses = manageNotificationUseCase.getScheduledNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Zamanlanmış bildirimler getirildi", responses)
        );
    }

    @GetMapping("/failed")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getFailedNotifications() {
        log.info("REST: Fetching failed notifications");
        List<NotificationResponse> responses = manageNotificationUseCase.getFailedNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Başarısız bildirimler getirildi", responses)
        );
    }

    // --- NOTIFICATION ACTIONS (PATCH) ---

    @PatchMapping("/{id}/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(@PathVariable UUID id) {
        log.info("REST: Sending notification with ID: {}", id);
        manageNotificationUseCase.sendNotification(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim gönderildi", null)
        );
    }

    @PostMapping("/send-pending")
    public ResponseEntity<ApiResponse<Void>> sendPendingNotifications() {
        log.info("REST: Sending all pending notifications");
        manageNotificationUseCase.sendPendingNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Bekleyen bildirimler gönderildi", null)
        );
    }

    @PatchMapping("/{id}/mark-sent")
    public ResponseEntity<ApiResponse<Void>> markAsSent(@PathVariable UUID id) {
        log.info("REST: Marking notification as sent: {}", id);
        manageNotificationUseCase.markAsSent(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim gönderildi olarak işaretlendi", null)
        );
    }

    @PatchMapping("/{id}/mark-delivered")
    public ResponseEntity<ApiResponse<Void>> markAsDelivered(@PathVariable UUID id) {
        log.info("REST: Marking notification as delivered: {}", id);
        manageNotificationUseCase.markAsDelivered(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim iletildi olarak işaretlendi", null)
        );
    }

    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        log.info("REST: Marking notification as read: {}", id);
        manageNotificationUseCase.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim okundu olarak işaretlendi", null)
        );
    }

    @PatchMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<Void>> retryFailedNotification(@PathVariable UUID id) {
        log.info("REST: Retrying failed notification: {}", id);
        manageNotificationUseCase.retryFailedNotification(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bildirim tekrar denendi", null)
        );
    }

    // --- SCHEDULED NOTIFICATIONS ---

    @GetMapping("/due-for-sending")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsDueForSending() {
        log.info("REST: Fetching notifications due for sending");
        List<NotificationResponse> responses = manageNotificationUseCase.getNotificationsDueForSending();

        return ResponseEntity.ok(
                ApiResponse.success("Gönderilmeyi bekleyen bildirimler getirildi", responses)
        );
    }

    @PostMapping("/process-scheduled")
    public ResponseEntity<ApiResponse<Void>> processScheduledNotifications() {
        log.info("REST: Processing scheduled notifications");
        manageNotificationUseCase.processScheduledNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Zamanlanmış bildirimler işlendi", null)
        );
    }

    // --- STATISTICS ---

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalNotificationCount() {
        return ResponseEntity.ok(
                ApiResponse.success("Toplam bildirim sayısı",
                        manageNotificationUseCase.getTotalNotificationCount())
        );
    }

    @GetMapping("/patient/{patientId}/count")
    public ResponseEntity<ApiResponse<Long>> getNotificationCountByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait bildirim sayısı",
                        manageNotificationUseCase.getNotificationCountByPatient(patientId))
        );
    }

    @GetMapping("/patient/{patientId}/count/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable UUID patientId) {
        return ResponseEntity.ok(
                ApiResponse.success("Okunmamış bildirim sayısı",
                        manageNotificationUseCase.getUnreadCount(patientId))
        );
    }
}