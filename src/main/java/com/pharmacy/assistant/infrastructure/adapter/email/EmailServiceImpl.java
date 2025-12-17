package com.pharmacy.assistant.infrastructure.adapter.email;

import com.pharmacy.assistant.application.port.output.EmailService;
import com.pharmacy.assistant.domain.exception.NotificationSendException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Email Service Implementation using Gmail SMTP
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${pharmacy.email.from:noreply@pharmacy.com}")
    private String fromEmail;

    @Value("${pharmacy.email.from-name:Eczane Dijital Asistan}")
    private String fromName;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("Sending simple email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("Simple email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new NotificationSendException("Email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Sending HTML email to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);

            log.info("HTML email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new NotificationSendException("HTML email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendTemplateEmail(String to, String subject, String templateName,
                                  Map<String, Object> templateModel) {
        try {
            log.info("Sending template email to: {} with template: {}", to, templateName);

            Context context = new Context();
            context.setVariables(templateModel);

            String htmlContent = templateEngine.process(templateName, context);

            sendHtmlEmail(to, subject, htmlContent);

            log.info("Template email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send template email to: {}", to, e);
            throw new NotificationSendException("Template email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMedicationReminderEmail(String to, String patientName,
                                            String medicationName, String dosage) {
        try {
            log.info("Sending medication reminder email to: {}", to);

            String subject = "🔔 İlaç Hatırlatması - " + medicationName;

            String htmlContent = buildMedicationReminderHtml(patientName, medicationName, dosage);

            sendHtmlEmail(to, subject, htmlContent);

            log.info("Medication reminder email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send medication reminder email to: {}", to, e);
            throw new NotificationSendException(
                    "İlaç hatırlatma emaili gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendPrescriptionExpiryEmail(String to, String patientName,
                                            String prescriptionNumber, int daysRemaining) {
        try {
            log.info("Sending prescription expiry email to: {}", to);

            String subject = "⚠️ Reçete Yenileme Hatırlatması - " + prescriptionNumber;

            String htmlContent = buildPrescriptionExpiryHtml(
                    patientName, prescriptionNumber, daysRemaining);

            sendHtmlEmail(to, subject, htmlContent);

            log.info("Prescription expiry email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send prescription expiry email to: {}", to, e);
            throw new NotificationSendException(
                    "Reçete hatırlatma emaili gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Builds HTML content for medication reminder
     */
    private String buildMedicationReminderHtml(String patientName,
                                               String medicationName, String dosage) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                             color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .medication-box { background: white; padding: 20px; margin: 20px 0; 
                                     border-left: 4px solid #667eea; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .button { display: inline-block; padding: 12px 24px; background: #667eea; 
                             color: white; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔔 İlaç Hatırlatması</h1>
                    </div>
                    <div class="content">
                        <p>Sayın <strong>%s</strong>,</p>
                        
                        <div class="medication-box">
                            <h2 style="color: #667eea; margin-top: 0;">💊 %s</h2>
                            <p><strong>Doz:</strong> %s</p>
                            <p>İlacınızı alma zamanı geldi.</p>
                        </div>
                        
                        <p>Sağlığınız için düzenli ilaç kullanımı çok önemlidir. 
                           Lütfen doktorunuzun önerdiği şekilde kullanmaya devam edin.</p>
                        
                        <p><strong>Not:</strong> İlaç kullanımı konusunda sorularınız varsa 
                           lütfen doktorunuza veya eczacınıza danışın.</p>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <p>Sağlıklı günler dileriz! 💚</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Bu otomatik bir hatırlatma mesajıdır.</p>
                        <p>© 2024 Eczane Dijital Asistan</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName, medicationName, dosage);
    }

    /**
     * Builds HTML content for prescription expiry
     */
    private String buildPrescriptionExpiryHtml(String patientName,
                                               String prescriptionNumber, int daysRemaining) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); 
                             color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .warning-box { background: #fff3cd; border-left: 4px solid #ffc107; 
                                   padding: 20px; margin: 20px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚠️ Reçete Yenileme Hatırlatması</h1>
                    </div>
                    <div class="content">
                        <p>Sayın <strong>%s</strong>,</p>
                        
                        <div class="warning-box">
                            <h2 style="color: #856404; margin-top: 0;">📋 Reçete No: %s</h2>
                            <p><strong>Kalan Süre:</strong> %d gün</p>
                            <p style="color: #856404; font-weight: bold;">
                                Reçetenizin geçerlilik süresi dolmak üzere!
                            </p>
                        </div>
                        
                        <p>İlaçlarınızı kesintisiz kullanabilmeniz için lütfen 
                           <strong>doktorunuzdan yeni reçete almanızı</strong> unutmayın.</p>
                        
                        <h3>Yapmanız Gerekenler:</h3>
                        <ul>
                            <li>Doktorunuz ile randevu alın</li>
                            <li>Yeni reçete talebinizi iletin</li>
                            <li>Reçete yenilendiğinde eczanemizi ziyaret edin</li>
                        </ul>
                        
                        <p><strong>Önemli:</strong> Reçete süresi dolmadan işlem yapmanız 
                           ilaç kullanımınızda aksama olmaması için önemlidir.</p>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <p>Sağlıklı günler dileriz! 💚</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Bu otomatik bir hatırlatma mesajıdır.</p>
                        <p>© 2024 Eczane Dijital Asistan</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName, prescriptionNumber, daysRemaining);
    }
}