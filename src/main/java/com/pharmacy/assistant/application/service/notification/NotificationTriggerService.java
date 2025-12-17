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

    @Transactional
    public int checkAndNotifyExpiringMedications(int daysThreshold) {
        log.info("🔍 Checking for medications expiring within {} days", daysThreshold);

        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);

        List<Medication> expiringMedications = medicationRepository.findExpiringBetween(today, thresholdDate);
        log.info("📊 Found {} expiring medications", expiringMedications.size());

        int notificationCount = 0;

        for (Medication medication : expiringMedications) {
            try {
                Patient patient = patientRepository.findById(medication.getPatientId()).orElse(null);

                if (patient == null) {
                    log.warn("⚠️ Patient not found for medication: {}", medication.getId());
                    continue;
                }

                log.info("👤 Processing medication for patient: {} ({})",
                        patient.getFullName(), patient.getContactInfo().getEmail());

                if (!patient.canReceiveEmail()) {
                    log.warn("⚠️ Patient {} cannot receive email (email: {})",
                            patient.getId(), patient.getContactInfo().getEmail());
                    continue;
                }

                // ✅ Bildirim oluştur
                Notification notification = createMedicationExpiryNotification(medication, patient);
                notification = notificationRepository.save(notification); // ✅ SAVE EDİYORUZ
                log.info("✅ Notification created with ID: {}", notification.getId());

                // ✅ Email gönder VE notification'ı güncelle
                boolean emailSent = sendMedicationExpiryEmail(notification, medication, patient);

                if (emailSent) {
                    notification.markAsSent();
                    notificationRepository.save(notification); // ✅ DURUMU GÜNCELLEYEREK KAYDET
                    notificationCount++;
                    log.info("✅ Email sent successfully to: {}", patient.getContactInfo().getEmail());
                } else {
                    notification.markAsFailed("Email gönderilemedi");
                    notificationRepository.save(notification); // ✅ HATA DURUMUNU KAYDET
                    log.error("❌ Failed to send email to: {}", patient.getContactInfo().getEmail());
                }

            } catch (Exception e) {
                log.error("❌ Failed to process medication notification: {}", medication.getId(), e);
            }
        }

        log.info("🎯 Created {} medication expiry notifications", notificationCount);
        return notificationCount;
    }

    @Transactional
    public int checkAndNotifyExpiringPrescriptions(int daysThreshold) {
        log.info("🔍 Checking for prescriptions expiring within {} days", daysThreshold);

        List<Prescription> expiringPrescriptions = prescriptionRepository.findExpiringSoon(daysThreshold);
        log.info("📊 Found {} expiring prescriptions", expiringPrescriptions.size());

        int notificationCount = 0;

        for (Prescription prescription : expiringPrescriptions) {
            try {
                Patient patient = patientRepository.findById(prescription.getPatientId()).orElse(null);

                if (patient == null) {
                    log.warn("⚠️ Patient not found for prescription: {}", prescription.getId());
                    continue;
                }

                log.info("👤 Processing prescription for patient: {} ({})",
                        patient.getFullName(), patient.getContactInfo().getEmail());

                if (!patient.canReceiveEmail()) {
                    log.warn("⚠️ Patient {} cannot receive email", patient.getId());
                    continue;
                }

                // ✅ Bildirim oluştur
                Notification notification = createPrescriptionExpiryNotification(prescription, patient);
                notification = notificationRepository.save(notification);
                log.info("✅ Notification created with ID: {}", notification.getId());

                // ✅ Email gönder VE notification'ı güncelle
                boolean emailSent = sendPrescriptionExpiryEmail(notification, prescription, patient);

                if (emailSent) {
                    notification.markAsSent();
                    notificationRepository.save(notification);
                    notificationCount++;
                    log.info("✅ Email sent successfully to: {}", patient.getContactInfo().getEmail());
                } else {
                    notification.markAsFailed("Email gönderilemedi");
                    notificationRepository.save(notification);
                    log.error("❌ Failed to send email to: {}", patient.getContactInfo().getEmail());
                }

            } catch (Exception e) {
                log.error("❌ Failed to process prescription notification: {}", prescription.getId(), e);
            }
        }

        log.info("🎯 Created {} prescription expiry notifications", notificationCount);
        return notificationCount;
    }

    @Transactional
    public NotificationCheckResult checkAndCreateAllNotifications() {
        log.info("🚀 Starting comprehensive notification check...");

        NotificationCheckResult result = new NotificationCheckResult();
        result.medicationNotifications = checkAndNotifyExpiringMedications(7);
        result.prescriptionNotifications = checkAndNotifyExpiringPrescriptions(7);
        result.stockNotifications = checkAndNotifyExpiringStocks(90);

        log.info("🏁 Notification check completed. Results: {}", result);
        return result;
    }

    @Transactional
    public int checkAndNotifyExpiringStocks(int daysThreshold) {
        log.info("🔍 Checking for stocks expiring within {} days", daysThreshold);

        List<Stock> expiringStocks = stockRepository.findExpiringSoon(daysThreshold);
        int notificationCount = 0;

        for (Stock stock : expiringStocks) {
            try {
                List<Notification> existingNotifications = notificationRepository
                        .findByRelatedEntityIdAndType(stock.getId(), "STOCK");

                boolean recentNotificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getCreatedAt().isAfter(
                                java.time.LocalDateTime.now().minusDays(30)));

                if (recentNotificationExists) {
                    continue;
                }

                notificationUseCase.createStockExpiryNotification(stock.getId());
                notificationCount++;

            } catch (Exception e) {
                log.error("❌ Failed to create notification for stock: {}", stock.getId(), e);
            }
        }

        log.info("🎯 Created {} stock expiry notifications", notificationCount);
        return notificationCount;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Notification createMedicationExpiryNotification(Medication medication, Patient patient) {
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

    private Notification createPrescriptionExpiryNotification(Prescription prescription, Patient patient) {
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

    // ✅ BOOL DÖNDÜRÜYOR - BAŞARILI MI DEĞİL Mİ
    private boolean sendMedicationExpiryEmail(Notification notification, Medication medication, Patient patient) {
        try {
            log.info("📧 Attempting to send medication expiry email...");
            log.info("   📨 To: {}", patient.getContactInfo().getEmail());
            log.info("   👤 Patient: {}", patient.getFullName());

            Product product = productRepository.findById(medication.getProductId()).orElse(null);
            String medicationName = product != null ? product.getName() : medication.getMedicationName();
            String dosage = medication.getDosage() != null ?
                    medication.getDosage().getFormattedDosage() : "Belirtilmemiş";

            log.info("   💊 Medication: {} ({})", medicationName, dosage);

            emailService.sendMedicationReminderEmail(
                    patient.getContactInfo().getEmail(),
                    patient.getFullName(),
                    medicationName,
                    dosage
            );

            log.info("✅ Medication expiry email sent successfully!");
            return true;

        } catch (Exception e) {
            log.error("❌ Failed to send medication expiry email", e);
            log.error("   📧 Email: {}", patient.getContactInfo().getEmail());
            log.error("   🔴 Error: {}", e.getMessage());
            return false;
        }
    }

    // ✅ BOOL DÖNDÜRÜYOR
    private boolean sendPrescriptionExpiryEmail(Notification notification, Prescription prescription, Patient patient) {
        try {
            log.info("📧 Attempting to send prescription expiry email...");
            log.info("   📨 To: {}", patient.getContactInfo().getEmail());

            emailService.sendPrescriptionExpiryEmail(
                    patient.getContactInfo().getEmail(),
                    patient.getFullName(),
                    prescription.getPrescriptionNumber(),
                    (int) prescription.getRemainingDays()
            );

            log.info("✅ Prescription expiry email sent successfully!");
            return true;

        } catch (Exception e) {
            log.error("❌ Failed to send prescription expiry email", e);
            log.error("   📧 Email: {}", patient.getContactInfo().getEmail());
            log.error("   🔴 Error: {}", e.getMessage());
            return false;
        }
    }

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