package com.pharmacy.assistant.application.service.notification;

import com.pharmacy.assistant.application.port.input.ManageNotificationUseCase;
import com.pharmacy.assistant.application.port.output.*;
import com.pharmacy.assistant.domain.enums.NotificationChannel;
import com.pharmacy.assistant.domain.enums.NotificationStatus;
import com.pharmacy.assistant.domain.enums.NotificationType;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Notification Trigger Service
 * Checks and creates notifications for expiring medications and prescriptions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerService {

    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final StockRepository stockRepository;
    private final PatientRepository patientRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final ManageNotificationUseCase notificationUseCase;
    private final EmailService emailService;

    /**
     * Checks all medications and creates notifications for expiring ones
     * Returns count of notifications created
     */
    @Transactional
    public int checkAndNotifyExpiringMedications(int daysThreshold) {
        log.info("Checking for medications expiring within {} days", daysThreshold);

        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);

        List<Medication> expiringMedications =
                medicationRepository.findExpiringBetween(today, thresholdDate);

        int notificationCount = 0;

        for (Medication medication : expiringMedications) {
            try {
                // Hasta bilgisini getir
                Patient patient = patientRepository.findById(medication.getPatientId())
                        .orElse(null);

                if (patient == null) {
                    log.warn("Patient not found for medication: {}", medication.getId());
                    continue;
                }

                // Email almayı kabul etmişse ve email varsa
                if (!patient.canReceiveEmail()) {
                    log.info("Patient {} cannot receive email, skipping", patient.getId());
                    continue;
                }

                // Bu ilaç için daha önce bildirim gönderilmiş mi kontrol et
                List<Notification> existingNotifications = notificationRepository
                        .findByRelatedEntityIdAndType(
                                medication.getId(),
                                "MEDICATION"
                        );

                // Son 7 gün içinde gönderilmiş bildirim varsa tekrar gönderme
                boolean recentNotificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getCreatedAt().isAfter(
                                java.time.LocalDateTime.now().minusDays(7)
                        ));

                if (recentNotificationExists) {
                    log.info("Recent notification exists for medication: {}", medication.getId());
                    continue;
                }

                // Bildirim oluştur
                Notification notification = createMedicationExpiryNotification(
                        medication,
                        patient
                );

                notificationRepository.save(notification);

                // Email gönder
                sendMedicationExpiryEmail(notification, medication, patient);

                notificationCount++;
                log.info("Notification created and sent for medication: {}", medication.getId());

            } catch (Exception e) {
                log.error("Failed to create notification for medication: {}",
                        medication.getId(), e);
            }
        }

        log.info("Created {} medication expiry notifications", notificationCount);
        return notificationCount;
    }

    /**
     * Checks all prescriptions and creates notifications for expiring ones
     * Returns count of notifications created
     */
    @Transactional
    public int checkAndNotifyExpiringPrescriptions(int daysThreshold) {
        log.info("Checking for prescriptions expiring within {} days", daysThreshold);

        List<Prescription> expiringPrescriptions =
                prescriptionRepository.findExpiringSoon(daysThreshold);

        int notificationCount = 0;

        for (Prescription prescription : expiringPrescriptions) {
            try {
                // Hasta bilgisini getir
                Patient patient = patientRepository.findById(prescription.getPatientId())
                        .orElse(null);

                if (patient == null) {
                    log.warn("Patient not found for prescription: {}", prescription.getId());
                    continue;
                }

                // Email almayı kabul etmişse ve email varsa
                if (!patient.canReceiveEmail()) {
                    log.info("Patient {} cannot receive email, skipping", patient.getId());
                    continue;
                }

                // Bu reçete için daha önce bildirim gönderilmiş mi kontrol et
                List<Notification> existingNotifications = notificationRepository
                        .findByRelatedEntityIdAndType(
                                prescription.getId(),
                                "PRESCRIPTION"
                        );

                // Son 7 gün içinde gönderilmiş bildirim varsa tekrar gönderme
                boolean recentNotificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getCreatedAt().isAfter(
                                java.time.LocalDateTime.now().minusDays(7)
                        ));

                if (recentNotificationExists) {
                    log.info("Recent notification exists for prescription: {}",
                            prescription.getId());
                    continue;
                }

                // Bildirim oluştur
                Notification notification = createPrescriptionExpiryNotification(
                        prescription,
                        patient
                );

                notificationRepository.save(notification);

                // Email gönder
                sendPrescriptionExpiryEmail(notification, prescription, patient);

                notificationCount++;
                log.info("Notification created and sent for prescription: {}",
                        prescription.getId());

            } catch (Exception e) {
                log.error("Failed to create notification for prescription: {}",
                        prescription.getId(), e);
            }
        }

        log.info("Created {} prescription expiry notifications", notificationCount);
        return notificationCount;
    }

    /**
     * Checks and creates all types of notifications
     */
    @Transactional
    public NotificationCheckResult checkAndCreateAllNotifications() {
        log.info("Starting comprehensive notification check...");

        NotificationCheckResult result = new NotificationCheckResult();

        // İlaç bildirimleri (7 gün önceden)
        result.medicationNotifications = checkAndNotifyExpiringMedications(7);

        // Reçete bildirimleri (7 gün önceden)
        result.prescriptionNotifications = checkAndNotifyExpiringPrescriptions(7);

        // Stok bildirimleri (90 gün önceden - SKT için)
        result.stockNotifications = checkAndNotifyExpiringStocks(90);

        log.info("Notification check completed. Results: {}", result);
        return result;
    }

    /**
     * Checks stocks and creates notifications for near-expiry items
     */
    @Transactional
    public int checkAndNotifyExpiringStocks(int daysThreshold) {
        log.info("Checking for stocks expiring within {} days", daysThreshold);

        List<Stock> expiringStocks = stockRepository.findExpiringSoon(daysThreshold);
        int notificationCount = 0;

        for (Stock stock : expiringStocks) {
            try {
                // Bu stok için daha önce bildirim gönderilmiş mi kontrol et
                List<Notification> existingNotifications = notificationRepository
                        .findByRelatedEntityIdAndType(stock.getId(), "STOCK");

                boolean recentNotificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getCreatedAt().isAfter(
                                java.time.LocalDateTime.now().minusDays(30)
                        ));

                if (recentNotificationExists) {
                    continue;
                }

                // Sistem bildirimi oluştur (eczane yöneticisi için)
                notificationUseCase.createStockExpiryNotification(stock.getId());
                notificationCount++;

            } catch (Exception e) {
                log.error("Failed to create notification for stock: {}", stock.getId(), e);
            }
        }

        log.info("Created {} stock expiry notifications", notificationCount);
        return notificationCount;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Notification createMedicationExpiryNotification(Medication medication,
                                                            Patient patient) {
        Product product = productRepository.findById(medication.getProductId()).orElse(null);
        String medicationName = product != null ? product.getName() : medication.getMedicationName();

        Notification notification = new Notification();
        notification.setPatientId(patient.getId());
        notification.setType(NotificationType.MEDICATION_EXPIRY);
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setTitle("⏰ İlaç Reçete Yenileme Hatırlatması");
        notification.setMessage(String.format(
                "Sayın %s, %s ilacınızın reçete süresi %d gün içinde dolacaktır. " +
                        "Lütfen doktorunuzdan yeni reçete almanızı unutmayın.",
                patient.getFullName(),
                medicationName,
                medication.getSchedule().getRemainingDays()
        ));
        notification.setRecipient(patient.getContactInfo().getEmail());
        notification.setRelatedEntityId(medication.getId());
        notification.setRelatedEntityType("MEDICATION");
        notification.setPriority(4);
        notification.setRetryCount(0);
        notification.setMaxRetries(3);
        notification.prePersist();

        return notification;
    }

    private Notification createPrescriptionExpiryNotification(Prescription prescription,
                                                              Patient patient) {
        Notification notification = new Notification();
        notification.setPatientId(patient.getId());
        notification.setType(NotificationType.PRESCRIPTION_EXPIRY);
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setTitle("📋 Reçete Geçerlilik Süresi Uyarısı");
        notification.setMessage(String.format(
                "Sayın %s, %s numaralı reçetenizin geçerlilik süresi %d gün içinde dolacaktır. " +
                        "Lütfen yeni reçete almanızı unutmayın.",
                patient.getFullName(),
                prescription.getPrescriptionNumber(),
                prescription.getRemainingDays()
        ));
        notification.setRecipient(patient.getContactInfo().getEmail());
        notification.setRelatedEntityId(prescription.getId());
        notification.setRelatedEntityType("PRESCRIPTION");
        notification.setPriority(4);
        notification.setRetryCount(0);
        notification.setMaxRetries(3);
        notification.prePersist();

        return notification;
    }

    private void sendMedicationExpiryEmail(Notification notification,
                                           Medication medication,
                                           Patient patient) {
        try {
            Product product = productRepository.findById(medication.getProductId()).orElse(null);
            String medicationName = product != null ? product.getName() : medication.getMedicationName();
            String dosage = medication.getDosage() != null ?
                    medication.getDosage().getFormattedDosage() : "Belirtilmemiş";

            emailService.sendMedicationReminderEmail(
                    patient.getContactInfo().getEmail(),
                    patient.getFullName(),
                    medicationName,
                    dosage
            );

            notification.markAsSent();
            log.info("Medication expiry email sent to: {}", patient.getContactInfo().getEmail());

        } catch (Exception e) {
            log.error("Failed to send medication expiry email", e);
            notification.markAsFailed(e.getMessage());
        }
    }

    private void sendPrescriptionExpiryEmail(Notification notification,
                                             Prescription prescription,
                                             Patient patient) {
        try {
            emailService.sendPrescriptionExpiryEmail(
                    patient.getContactInfo().getEmail(),
                    patient.getFullName(),
                    prescription.getPrescriptionNumber(),
                    (int) prescription.getRemainingDays()
            );

            notification.markAsSent();
            log.info("Prescription expiry email sent to: {}", patient.getContactInfo().getEmail());

        } catch (Exception e) {
            log.error("Failed to send prescription expiry email", e);
            notification.markAsFailed(e.getMessage());
        }
    }

    // Inner class for result
    public static class NotificationCheckResult {
        public int medicationNotifications = 0;
        public int prescriptionNotifications = 0;
        public int stockNotifications = 0;

        public int getTotalNotifications() {
            return medicationNotifications + prescriptionNotifications + stockNotifications;
        }

        @Override
        public String toString() {
            return String.format(
                    "Medications: %d, Prescriptions: %d, Stocks: %d, Total: %d",
                    medicationNotifications,
                    prescriptionNotifications,
                    stockNotifications,
                    getTotalNotifications()
            );
        }
    }
}