// NotificationRecipient.java
package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for Notification Recipient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipient {
    private String name;          // Alıcı adı
    private String phoneNumber;   // Telefon numarası (SMS için)
    private String email;         // E-posta adresi (Email için)
    private String language;      // Dil tercihi (tr, en, vb.)

    /**
     * Validates if recipient has required information for SMS
     */
    public boolean canReceiveSms() {
        return phoneNumber != null &&
                !phoneNumber.isEmpty() &&
                isValidPhoneNumber(phoneNumber);
    }

    /**
     * Validates if recipient has required information for Email
     */
    public boolean canReceiveEmail() {
        return email != null &&
                !email.isEmpty() &&
                isValidEmail(email);
    }

    /**
     * Checks if recipient information is valid
     */
    public boolean isValid() {
        return name != null &&
                !name.isEmpty() &&
                (canReceiveSms() || canReceiveEmail());
    }

    /**
     * Gets formatted name for display
     */
    public String getFormattedName() {
        return name != null ? name : "Unknown";
    }

    /**
     * Gets formatted phone number
     */
    public String getFormattedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }
        // Türkiye formatı: +90 (5XX) XXX XX XX
        if (phoneNumber.startsWith("+90") && phoneNumber.length() == 13) {
            return String.format("+90 (%s) %s %s %s",
                    phoneNumber.substring(3, 6),
                    phoneNumber.substring(6, 9),
                    phoneNumber.substring(9, 11),
                    phoneNumber.substring(11, 13)
            );
        }
        return phoneNumber;
    }

    /**
     * Gets masked phone number for privacy
     */
    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }

    /**
     * Gets masked email for privacy
     */
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return "****@****.***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return "**@" + domain;
        }
        return localPart.substring(0, 2) + "****@" + domain;
    }

    /**
     * Simple phone number validation
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        // Basit kontrol: + ile başlayabilir, sonra sadece rakamlar
        String cleaned = phone.replaceAll("[^0-9+]", "");
        return cleaned.length() >= 10 && cleaned.length() <= 15;
    }

    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Gets preferred language or default
     */
    public String getLanguageOrDefault() {
        return language != null && !language.isEmpty() ? language : "tr";
    }
}

// ---

