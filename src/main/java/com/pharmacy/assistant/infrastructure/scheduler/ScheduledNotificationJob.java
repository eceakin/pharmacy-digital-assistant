package com.pharmacy.assistant.infrastructure.scheduler;

import com.pharmacy.assistant.application.service.notification.NotificationTriggerService;
import com.pharmacy.assistant.application.service.settings.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Scheduled Job for Automatic Notification Checks
 * Runs daily at the time configured in system settings
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledNotificationJob {

    private final NotificationTriggerService notificationTriggerService;
    private final SystemSettingsService settingsService;

    /**
     * Runs every day at 9:00 AM by default
     * Checks system settings for configured notification time
     *
     * Cron Expression: 0 0 9 * * ? = Every day at 09:00
     * You can change this to match your default notification time
     */
    @Scheduled(cron = "0 0 9 * * ?") // Her gün 09:00'da çalışır
    public void checkAndSendNotifications() {
        log.info("⏰ ========================================");
        log.info("⏰ SCHEDULED JOB STARTED");
        log.info("⏰ ========================================");

        try {
            // Sistemin bildirim saati ile mevcut saati karşılaştır
            LocalTime notificationTime = LocalTime.of(9, 0); // Default
            try {
                var settings = settingsService.getSettings();
                if (settings != null && settings.getNotificationTime() != null) {
                    notificationTime = settings.getNotificationTime();
                }
            } catch (Exception e) {
                log.warn("⚠️ Settings okunamadı, varsayılan saat kullanılıyor (09:00)");
            }

            LocalTime currentTime = LocalTime.now();
            log.info("🕐 Current time: {}", currentTime);
            log.info("🎯 Configured notification time: {}", notificationTime);

            // Mevcut saat, bildirim saatine yakınsa (±30 dakika tolerans) çalıştır
            boolean shouldRun = currentTime.isAfter(notificationTime.minusMinutes(30)) &&
                    currentTime.isBefore(notificationTime.plusMinutes(30));

            if (!shouldRun) {
                log.info("⏭️ Not the right time to send notifications. Skipping...");
                return;
            }

            log.info("✅ Starting notification checks...");

            // Tüm bildirimleri kontrol et ve gönder
            NotificationTriggerService.NotificationCheckResult result =
                    notificationTriggerService.checkAndCreateAllNotifications();

            log.info("📊 RESULTS:");
            log.info("   💊 Medication Notifications: {}", result.medicationNotifications);
            log.info("   📋 Prescription Notifications: {}", result.prescriptionNotifications);
            log.info("   📦 Stock Notifications: {}", result.stockNotifications);
            log.info("   🎯 TOTAL: {}", result.getTotalNotifications());

            if (result.getTotalNotifications() > 0) {
                log.info("✅ {} bildirim başarıyla gönderildi!", result.getTotalNotifications());
            } else {
                log.info("ℹ️ Gönderilecek bildirim bulunamadı.");
            }

        } catch (Exception e) {
            log.error("❌ SCHEDULED JOB FAILED", e);
        } finally {
            log.info("⏰ ========================================");
            log.info("⏰ SCHEDULED JOB COMPLETED");
            log.info("⏰ ========================================");
        }
    }

    /**
     * Optional: Manual trigger endpoint için
     * Her 5 dakikada bir kontrol yapar (test için)
     * Production'da bu metodu kapatabilirsiniz
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // 5 dakikada bir (test için)
    public void periodicCheck() {
        // Bu metod sadece development/test için
        // Production'da comment out yapabilirsiniz
        log.debug("🔍 Periodic check running... (For development only)");
    }

    /**
     * Health check - Runs every hour to ensure scheduler is alive
     */
    @Scheduled(fixedRate = 3600000) // Her saat
    public void healthCheck() {
        log.debug("💚 Scheduler health check - System is running");
    }
}