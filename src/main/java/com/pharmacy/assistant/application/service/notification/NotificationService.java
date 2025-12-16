package com.pharmacy.assistant.application.service.notification;

import com.pharmacy.assistant.application.dto.request.CreateNotificationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateNotificationRequest;
import com.pharmacy.assistant.application.dto.response.NotificationResponse;
import com.pharmacy.assistant.application.dto.response.NotificationSummaryResponse;
import com.pharmacy.assistant.application.mapper.NotificationMapper;
import com.pharmacy.assistant.application.port.input.ManageNotificationUseCase;
import com.pharmacy.assistant.application.port.output.*;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
import com.pharmacy.assistant.domain.exception.*;
import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.model.inventory.Stock;
import com.pharmacy.assistant.domain.model.notification.Notification;
import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.model.patient.Patient;
import com.pharmacy.assistant.domain.model.prescription.Prescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements ManageNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for patient: {}", request.getPatientId());

        // Hasta kontrolü
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + request.getPatientId()));

        // Kanal uygunluk kontrolü
        validateChannelForPatient(patient, request.getChannel());

        Notification notification = notificationMapper.toDomain(request);

        // Alıcı bilgisini set et
        setRecipientInfo(notification, patient);

        notification.prePersist();

        Notification savedNotification = notificationRepository.save(notification);

        log.info("Notification created successfully with ID: {}", savedNotification.getId());
        return notificationMapper.toResponse(
                savedNotification,
                patient.getFullName(),
                getRecipientInfo(patient, request.getChannel())
        );
    }

    @Override
    public NotificationResponse updateNotification(UUID id, UpdateNotificationRequest request) {
        log.info("Updating notification with ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        // Sadece PENDING veya SCHEDULED durumdaki bildirimler güncellenebilir
        if (!notification.getStatus().equals(NotificationStatus.PENDING) &&
                !notification.getStatus().equals(NotificationStatus.SCHEDULED)) {
            throw new InvalidNotificationException(
                    "Sadece beklemede veya zamanlanmış bildirimler güncellenebilir");
        }

        notificationMapper.updateDomainFromRequest(notification, request);
        notification.prePersist();

        Notification updatedNotification = notificationRepository.save(notification);

        Patient patient = patientRepository.findById(notification.getPatientId()).orElse(null);

        log.info("Notification updated successfully: {}", id);
        return notificationMapper.toResponse(
                updatedNotification,
                patient != null ? patient.getFullName() : "Unknown",
                notification.getRecipient()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID id) {
        log.info("Fetching notification with ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        Patient patient = patientRepository.findById(notification.getPatientId()).orElse(null);

        return notificationMapper.toResponse(
                notification,
                patient != null ? patient.getFullName() : "Unknown",
                notification.getRecipient()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        log.info("Fetching all notifications");

        return notificationRepository.findAll().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationSummaryResponse> getAllNotificationsSummary() {
        log.info("Fetching all notifications summary");

        return notificationRepository.findAll().stream()
                .map(this::toSummaryResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotification(UUID id) {
        log.info("Deleting notification with ID: {}", id);

        if (!notificationRepository.existsById(id)) {
            throw new NotificationNotFoundException("Bildirim bulunamadı: " + id);
        }

        notificationRepository.deleteById(id);
        log.info("Notification deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByPatientId(UUID patientId) {
        log.info("Fetching notifications for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return notificationRepository.findByPatientId(patientId).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        log.info("Fetching notifications by status: {}", status);

        return notificationRepository.findByStatus(status).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(NotificationType type) {
        log.info("Fetching notifications by type: {}", type);

        return notificationRepository.findByType(type).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByChannel(NotificationChannel channel) {
        log.info("Fetching notifications by channel: {}", channel);

        return notificationRepository.findByChannel(channel).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getPendingNotifications() {
        log.info("Fetching pending notifications");

        return notificationRepository.findPendingNotifications().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getScheduledNotifications() {
        log.info("Fetching scheduled notifications");

        return notificationRepository.findScheduledNotifications().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getSentNotifications() {
        log.info("Fetching sent notifications");

        return notificationRepository.findSentNotifications().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getFailedNotifications() {
        log.info("Fetching failed notifications");

        return notificationRepository.findFailedNotifications().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(UUID patientId) {
        log.info("Fetching unread notifications for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return notificationRepository.findUnreadByPatientId(patientId).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    public void sendNotification(UUID id) {
        log.info("Sending notification with ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        if (!notification.canBeSent()) {
            throw new InvalidNotificationException(
                    "Bu bildirim gönderilemez. Durum: " + notification.getStatus());
        }

        try {
            // Burada gerçek SMS/Email gönderimi yapılır
            // Şimdilik sadece durum değişimi yapıyoruz
            notification.markAsSent();
            notificationRepository.save(notification);

            log.info("Notification sent successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", id, e);
            notification.markAsFailed(e.getMessage());
            notificationRepository.save(notification);
            throw new NotificationSendException("Bildirim gönderilemedi: " + e.getMessage());
        }
    }

    @Override
    public void sendPendingNotifications() {
        log.info("Sending all pending notifications");

        List<Notification> pendingNotifications = notificationRepository.findPendingNotifications();

        for (Notification notification : pendingNotifications) {
            try {
                sendNotification(notification.getId());
            } catch (Exception e) {
                log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
                // Devam et, diğer bildirimleri göndermeye çalış
            }
        }

        log.info("Finished sending pending notifications. Total: {}", pendingNotifications.size());
    }

    @Override
    public void markAsSent(UUID id) {
        log.info("Marking notification as sent: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        notification.markAsSent();
        notificationRepository.save(notification);

        log.info("Notification marked as sent: {}", id);
    }

    @Override
    public void markAsDelivered(UUID id) {
        log.info("Marking notification as delivered: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        notification.markAsDelivered();
        notificationRepository.save(notification);

        log.info("Notification marked as delivered: {}", id);
    }

    @Override
    public void markAsRead(UUID id) {
        log.info("Marking notification as read: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        notification.markAsRead();
        notificationRepository.save(notification);

        log.info("Notification marked as read: {}", id);
    }

    @Override
    public void markAsFailed(UUID id, String errorMessage) {
        log.info("Marking notification as failed: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        notification.markAsFailed(errorMessage);
        notificationRepository.save(notification);

        log.info("Notification marked as failed: {}", id);
    }

    @Override
    public void retryFailedNotification(UUID id) {
        log.info("Retrying failed notification: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Bildirim bulunamadı: " + id));

        if (!notification.canBeRetried()) {
            throw new InvalidNotificationException(
                    "Bu bildirim tekrar denenemez. Maksimum deneme sayısına ulaşıldı.");
        }

        notification.retry();
        notificationRepository.save(notification);

        // Tekrar göndermeyi dene
        try {
            sendNotification(id);
        } catch (Exception e) {
            log.error("Retry failed for notification: {}", id, e);
        }

        log.info("Notification retry attempted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsDueForSending() {
        log.info("Fetching notifications due for sending");

        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findNotificationsDueForSending(now).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    public void processScheduledNotifications() {
        log.info("Processing scheduled notifications");

        List<Notification> dueNotifications =
                notificationRepository.findNotificationsDueForSending(LocalDateTime.now());

        for (Notification notification : dueNotifications) {
            try {
                // SCHEDULED durumundan PENDING'e çek
                notification.setStatus(NotificationStatus.PENDING);
                notificationRepository.save(notification);

                // Hemen gönder
                sendNotification(notification.getId());
            } catch (Exception e) {
                log.error("Failed to process scheduled notification {}: {}",
                        notification.getId(), e.getMessage());
            }
        }

        log.info("Processed {} scheduled notifications", dueNotifications.size());
    }

    @Override
    public void createMedicationExpiryNotification(UUID medicationId) {
        log.info("Creating medication expiry notification for: {}", medicationId);

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + medicationId));

        Patient patient = patientRepository.findById(medication.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(
                        "Hasta bulunamadı: " + medication.getPatientId()));

        // SMS onayı varsa SMS, yoksa email gönder
        NotificationChannel channel = patient.canReceiveSms() ?
                NotificationChannel.SMS : NotificationChannel.EMAIL;

        Notification notification = new Notification();
        notification.setPatientId(patient.getId());
        notification.setType(NotificationType.MEDICATION_EXPIRY);
        notification.setChannel(channel);
        notification.setTitle("İlaç Reçete Yenileme Hatırlatması");
        notification.setMessage(String.format(
                "Sayın %s, %s ilacınızın reçete süresi %d gün içinde dolacaktır. " +
                        "Lütfen doktorunuzdan yeni reçete almanızı unutmayın.",
                patient.getFullName(),
                medication.getMedicationName(),
                medication.getSchedule().getRemainingDays()
        ));
        notification.setRelatedEntityId(medicationId);
        notification.setRelatedEntityType("MEDICATION");
        notification.setPriority(4);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);

        setRecipientInfo(notification, patient);
        notification.prePersist();

        notificationRepository.save(notification);

        log.info("Medication expiry notification created successfully");
    }

    @Override
    public void createPrescriptionExpiryNotification(UUID prescriptionId) {
        log.info("Creating prescription expiry notification for: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(
                        "Reçete bulunamadı: " + prescriptionId));

        Patient patient = patientRepository.findById(prescription.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(
                        "Hasta bulunamadı: " + prescription.getPatientId()));

        NotificationChannel channel = patient.canReceiveSms() ?
                NotificationChannel.SMS : NotificationChannel.EMAIL;

        Notification notification = new Notification();
        notification.setPatientId(patient.getId());
        notification.setType(NotificationType.PRESCRIPTION_EXPIRY);
        notification.setChannel(channel);
        notification.setTitle("Reçete Geçerlilik Süresi Uyarısı");
        notification.setMessage(String.format(
                "Sayın %s, %s numaralı reçetenizin geçerlilik süresi %d gün içinde dolacaktır. " +
                        "Lütfen yeni reçete almanızı unutmayın.",
                patient.getFullName(),
                prescription.getPrescriptionNumber(),
                prescription.getRemainingDays()
        ));
        notification.setRelatedEntityId(prescriptionId);
        notification.setRelatedEntityType("PRESCRIPTION");
        notification.setPriority(4);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);

        setRecipientInfo(notification, patient);
        notification.prePersist();

        notificationRepository.save(notification);

        log.info("Prescription expiry notification created successfully");
    }

    @Override
    public void createStockLowNotification(UUID stockId) {
        log.info("Creating stock low notification for: {}", stockId);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + stockId));

        Product product = productRepository.findById(stock.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Ürün bulunamadı: " + stock.getProductId()));

        // Eczane yöneticisine sistem bildirimi
        // Burada hasta yerine eczane yöneticisinin ID'si kullanılmalı
        // Şimdilik genel bir bildirim oluşturuyoruz

        Notification notification = new Notification();
        notification.setType(NotificationType.STOCK_LOW);
        notification.setChannel(NotificationChannel.SYSTEM);
        notification.setTitle("Düşük Stok Uyarısı");
        notification.setMessage(String.format(
                "%s ürününün stoğu minimum seviyenin altına düştü. " +
                        "Mevcut: %d, Minimum: %d",
                product.getName(),
                stock.getQuantity(),
                stock.getMinimumStockLevel()
        ));
        notification.setRelatedEntityId(stockId);
        notification.setRelatedEntityType("STOCK");
        notification.setPriority(3);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);
        notification.prePersist();

        notificationRepository.save(notification);

        log.info("Stock low notification created successfully");
    }

    @Override
    public void createStockExpiryNotification(UUID stockId) {
        log.info("Creating stock expiry notification for: {}", stockId);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + stockId));

        Product product = productRepository.findById(stock.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Ürün bulunamadı: " + stock.getProductId()));

        Notification notification = new Notification();
        notification.setType(NotificationType.STOCK_EXPIRY);
        notification.setChannel(NotificationChannel.SYSTEM);
        notification.setTitle("Stok SKT Uyarısı");
        notification.setMessage(String.format(
                "%s ürününün (Parti: %s) son kullanma tarihi %d gün içinde dolacak.",
                product.getName(),
                stock.getBatchNumber(),
                stock.getDaysUntilExpiry()
        ));
        notification.setRelatedEntityId(stockId);
        notification.setRelatedEntityType("STOCK");
        notification.setPriority(3);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);
        notification.prePersist();

        notificationRepository.save(notification);

        log.info("Stock expiry notification created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalNotificationCount() {
        return notificationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByPatient(UUID patientId) {
        return notificationRepository.countByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByStatus(NotificationStatus status) {
        return notificationRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByType(NotificationType type) {
        return notificationRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID patientId) {
        return notificationRepository.countUnreadByPatientId(patientId);
    }

    // Helper methods
    private void validateChannelForPatient(Patient patient, NotificationChannel channel) {
        if (channel == NotificationChannel.SMS && !patient.canReceiveSms()) {
            throw new InvalidNotificationException(
                    "Hasta SMS almak için onay vermemiş veya telefon numarası eksik");
        }

        if (channel == NotificationChannel.EMAIL && !patient.canReceiveEmail()) {
            throw new InvalidNotificationException(
                    "Hastanın email adresi kayıtlı değil");
        }
    }

    private void setRecipientInfo(Notification notification, Patient patient) {
        String recipient = getRecipientInfo(patient, notification.getChannel());
        notification.setRecipient(recipient);
    }

    private String getRecipientInfo(Patient patient, NotificationChannel channel) {
        if (channel == NotificationChannel.SMS) {
            return patient.getContactInfo().getPhoneNumber();
        } else if (channel == NotificationChannel.EMAIL) {
            return patient.getContactInfo().getEmail();
        }
        return "SYSTEM";
    }

    private NotificationResponse toResponseWithPatient(Notification notification) {
        Patient patient = notification.getPatientId() != null ?
                patientRepository.findById(notification.getPatientId()).orElse(null) : null;

        return notificationMapper.toResponse(
                notification,
                patient != null ? patient.getFullName() : "System",
                notification.getRecipient()
        );
    }

    private NotificationSummaryResponse toSummaryResponseWithPatient(Notification notification) {
        Patient patient = notification.getPatientId() != null ?
                patientRepository.findById(notification.getPatientId()).orElse(null) : null;

        return notificationMapper.toSummaryResponse(
                notification,
                patient != null ? patient.getFullName() : "System"
        );
    }
}