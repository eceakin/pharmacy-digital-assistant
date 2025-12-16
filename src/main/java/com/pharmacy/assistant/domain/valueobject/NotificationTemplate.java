package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map; /**
 * Value Object for Notification Templates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    private String name;                      // Template adı
    private String subject;                   // Konu (Email için)
    private String messageTemplate;           // Mesaj şablonu (placeholders ile)
    private Map<String, String> variables;    // Değişkenler (key-value pairs)

    /**
     * Renders the template with variables
     */
    public String renderMessage() {
        if (messageTemplate == null) {
            return "";
        }

        String rendered = messageTemplate;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                rendered = rendered.replace(placeholder, entry.getValue());
            }
        }
        return rendered;
    }

    /**
     * Renders the subject with variables
     */
    public String renderSubject() {
        if (subject == null) {
            return "";
        }

        String rendered = subject;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                rendered = rendered.replace(placeholder, entry.getValue());
            }
        }
        return rendered;
    }

    /**
     * Adds a variable to the template
     */
    public void addVariable(String key, String value) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put(key, value);
    }

    /**
     * Checks if template is valid
     */
    public boolean isValid() {
        return name != null &&
                !name.isEmpty() &&
                messageTemplate != null &&
                !messageTemplate.isEmpty();
    }

    /**
     * Creates a medication reminder template
     */
    public static NotificationTemplate createMedicationReminder(
            String patientName,
            String medicationName,
            String dosage) {

        NotificationTemplate template = new NotificationTemplate();
        template.setName("medication_reminder");
        template.setSubject("İlaç Hatırlatması");
        template.setMessageTemplate(
                "Sayın {patientName}, {medicationName} ({dosage}) ilacınızı alma zamanı. Sağlıklı günler dileriz."
        );

        Map<String, String> vars = new HashMap<>();
        vars.put("patientName", patientName);
        vars.put("medicationName", medicationName);
        vars.put("dosage", dosage);
        template.setVariables(vars);

        return template;
    }

    /**
     * Creates a prescription renewal template
     */
    public static NotificationTemplate createPrescriptionRenewal(
            String patientName,
            String prescriptionNumber,
            int daysRemaining) {

        NotificationTemplate template = new NotificationTemplate();
        template.setName("prescription_renewal");
        template.setSubject("Reçete Yenileme Hatırlatması");
        template.setMessageTemplate(
                "Sayın {patientName}, {prescriptionNumber} numaralı reçetenizin geçerlilik süresi {daysRemaining} gün sonra dolacaktır. Lütfen yenileme işlemi için doktorunuza başvurunuz."
        );

        Map<String, String> vars = new HashMap<>();
        vars.put("patientName", patientName);
        vars.put("prescriptionNumber", prescriptionNumber);
        vars.put("daysRemaining", String.valueOf(daysRemaining));
        template.setVariables(vars);

        return template;
    }

    /**
     * Creates a stock alert template
     */
    public static NotificationTemplate createStockAlert(
            String productName,
            int quantity,
            String storeName) {

        NotificationTemplate template = new NotificationTemplate();
        template.setName("stock_alert");
        template.setSubject("Stok Uyarısı");
        template.setMessageTemplate(
                "{productName} ürününden {quantity} adet stokta mevcuttur. {storeName}"
        );

        Map<String, String> vars = new HashMap<>();
        vars.put("productName", productName);
        vars.put("quantity", String.valueOf(quantity));
        vars.put("storeName", storeName);
        template.setVariables(vars);

        return template;
    }
}
