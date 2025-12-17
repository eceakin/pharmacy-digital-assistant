package com.pharmacy.assistant.application.port.output;

/**
 * Email Service Port
 * Defines email sending operations
 */
public interface EmailService {

    /**
     * Sends a simple text email
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * Sends an HTML email
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * Sends an email with template
     */
    void sendTemplateEmail(String to, String subject, String templateName,
                           java.util.Map<String, Object> templateModel);

    /**
     * Sends a medication reminder email
     */
    void sendMedicationReminderEmail(String to, String patientName,
                                     String medicationName, String dosage);

    /**
     * Sends a prescription expiry email
     */
    void sendPrescriptionExpiryEmail(String to, String patientName,
                                     String prescriptionNumber, int daysRemaining);

    /**
     * Validates email address
     */
    boolean isValidEmail(String email);
}