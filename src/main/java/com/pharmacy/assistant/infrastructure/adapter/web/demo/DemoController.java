package com.pharmacy.assistant.infrastructure.adapter.web.demo;

import com.pharmacy.assistant.application.service.notification.NotificationTriggerService;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo Controller
 * For demonstration purposes - triggers notification checks manually
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final NotificationTriggerService notificationTriggerService;

    /**
     * DEMO: Check and send notifications for expiring medications
     * GET /api/demo/check-medications
     */
    @GetMapping("/check-medications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkExpiringMedications(
            @RequestParam(defaultValue = "7") int days) {

        log.info("🎯 DEMO: Checking medications expiring within {} days", days);

        int count = notificationTriggerService.checkAndNotifyExpiringMedications(days);

        Map<String, Object> result = new HashMap<>();
        result.put("checkedDaysAhead", days);
        result.put("notificationsSent", count);
        result.put("message", count > 0 ?
                "✅ " + count + " adet ilaç hatırlatma emaili gönderildi!" :
                "ℹ️ Süresi yaklaşan ilaç bulunamadı.");

        return ResponseEntity.ok(
                ApiResponse.success("İlaç kontrolleri tamamlandı", result)
        );
    }

    /**
     * DEMO: Check and send notifications for expiring prescriptions
     * GET /api/demo/check-prescriptions
     */
    @GetMapping("/check-prescriptions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkExpiringPrescriptions(
            @RequestParam(defaultValue = "7") int days) {

        log.info("🎯 DEMO: Checking prescriptions expiring within {} days", days);

        int count = notificationTriggerService.checkAndNotifyExpiringPrescriptions(days);

        Map<String, Object> result = new HashMap<>();
        result.put("checkedDaysAhead", days);
        result.put("notificationsSent", count);
        result.put("message", count > 0 ?
                "✅ " + count + " adet reçete hatırlatma emaili gönderildi!" :
                "ℹ️ Süresi yaklaşan reçete bulunamadı.");

        return ResponseEntity.ok(
                ApiResponse.success("Reçete kontrolleri tamamlandı", result)
        );
    }

    /**
     * DEMO: Check and send ALL types of notifications
     * GET /api/demo/check-all
     */
    @GetMapping("/check-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAllNotifications() {

        log.info("🎯 DEMO: Checking ALL notification types");

        NotificationTriggerService.NotificationCheckResult result =
                notificationTriggerService.checkAndCreateAllNotifications();

        Map<String, Object> response = new HashMap<>();
        response.put("medicationNotifications", result.medicationNotifications);
        response.put("prescriptionNotifications", result.prescriptionNotifications);
        response.put("stockNotifications", result.stockNotifications);
        response.put("totalNotifications", result.getTotalNotifications());
        response.put("message", result.getTotalNotifications() > 0 ?
                "✅ Toplam " + result.getTotalNotifications() + " bildirim gönderildi!" :
                "ℹ️ Gönderilecek bildirim bulunamadı.");

        return ResponseEntity.ok(
                ApiResponse.success("Tüm kontrollar tamamlandı", response)
        );
    }

    /**
     * DEMO: Get system status
     * GET /api/demo/status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, String>>> getDemoStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("system", "🟢 Çalışıyor");
        status.put("emailService", "✅ Aktif");
        status.put("notificationService", "✅ Aktif");
        status.put("demo", "🎬 Hazır");

        return ResponseEntity.ok(
                ApiResponse.success("Sistem durumu", status)
        );
    }
}