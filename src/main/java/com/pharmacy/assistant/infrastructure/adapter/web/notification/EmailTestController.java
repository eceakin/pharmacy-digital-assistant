package com.pharmacy.assistant.infrastructure.adapter.web.test;

import com.pharmacy.assistant.application.port.output.EmailService;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Email Test Controller
 * For testing email functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/test/email")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    /**
     * Test simple email
     * GET /api/test/email/simple?to=test@example.com
     */
    @GetMapping("/simple")
    public ResponseEntity<ApiResponse<String>> sendSimpleTestEmail(
            @RequestParam String to) {

        log.info("Sending simple test email to: {}", to);

        try {
            emailService.sendSimpleEmail(
                    to,
                    "Test Email - Eczane Dijital Asistan",
                    "Bu bir test emailidir. Eğer bu mesajı aldıysanız, email sistemi çalışıyor demektir! ✅"
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Test email başarıyla gönderildi!",
                            "Email: " + to)
            );
        } catch (Exception e) {
            log.error("Failed to send test email", e);
            return ResponseEntity.ok(
                    ApiResponse.error("Email gönderilemedi: " + e.getMessage())
            );
        }
    }

    /**
     * Test HTML email
     * GET /api/test/email/html?to=test@example.com
     */
    @GetMapping("/html")
    public ResponseEntity<ApiResponse<String>> sendHtmlTestEmail(
            @RequestParam String to) {

        log.info("Sending HTML test email to: {}", to);

        try {
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #667eea; color: white; padding: 20px; border-radius: 10px; }
                        .content { padding: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 HTML Email Test</h1>
                        </div>
                        <div class="content">
                            <p>Merhaba!</p>
                            <p>Bu bir <strong>HTML test emailidir</strong>.</p>
                            <p>Eğer bu mesajı güzel bir şekilde görüyorsanız, HTML email sistemi çalışıyor! ✅</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            emailService.sendHtmlEmail(
                    to,
                    "HTML Test Email - Eczane Dijital Asistan",
                    htmlContent
            );

            return ResponseEntity.ok(
                    ApiResponse.success("HTML test email başarıyla gönderildi!",
                            "Email: " + to)
            );
        } catch (Exception e) {
            log.error("Failed to send HTML test email", e);
            return ResponseEntity.ok(
                    ApiResponse.error("Email gönderilemedi: " + e.getMessage())
            );
        }
    }

    /**
     * Test medication reminder email
     * GET /api/test/email/medication?to=test@example.com&name=Ali&medication=Aspirin&dosage=500mg
     */
    @GetMapping("/medication")
    public ResponseEntity<ApiResponse<String>> sendMedicationTestEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Test Hasta") String name,
            @RequestParam(defaultValue = "Aspirin") String medication,
            @RequestParam(defaultValue = "500mg") String dosage) {

        log.info("Sending medication reminder test email to: {}", to);

        try {
            emailService.sendMedicationReminderEmail(to, name, medication, dosage);

            return ResponseEntity.ok(
                    ApiResponse.success("İlaç hatırlatma emaili başarıyla gönderildi!",
                            "Email: " + to)
            );
        } catch (Exception e) {
            log.error("Failed to send medication reminder email", e);
            return ResponseEntity.ok(
                    ApiResponse.error("Email gönderilemedi: " + e.getMessage())
            );
        }
    }

    /**
     * Test prescription expiry email
     * GET /api/test/email/prescription?to=test@example.com&name=Ali&prescriptionNo=RX123456&days=7
     */
    @GetMapping("/prescription")
    public ResponseEntity<ApiResponse<String>> sendPrescriptionTestEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Test Hasta") String name,
            @RequestParam(defaultValue = "RX123456") String prescriptionNo,
            @RequestParam(defaultValue = "7") int days) {

        log.info("Sending prescription expiry test email to: {}", to);

        try {
            emailService.sendPrescriptionExpiryEmail(to, name, prescriptionNo, days);

            return ResponseEntity.ok(
                    ApiResponse.success("Reçete hatırlatma emaili başarıyla gönderildi!",
                            "Email: " + to)
            );
        } catch (Exception e) {
            log.error("Failed to send prescription expiry email", e);
            return ResponseEntity.ok(
                    ApiResponse.error("Email gönderilemedi: " + e.getMessage())
            );
        }
    }
}