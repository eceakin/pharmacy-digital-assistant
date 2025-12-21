package com.pharmacy.assistant.domain.model.settings;

import com.pharmacy.assistant.domain.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * System Settings Domain Model
 * Stores application-wide configuration settings
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemSettings extends BaseEntity {

    // Bildirim Ayarları (Notification Settings)
    private Integer medicationExpiryWarningDays;    // İlaç bitim uyarısı (gün)
    private Integer prescriptionExpiryWarningDays;  // Reçete bitim uyarısı (gün)
    private Integer stockExpiryWarningDays;         // Stok SKT uyarısı (gün)
    private LocalTime notificationTime;             // Bildirim gönderim saati

    // Email Ayarları
    private Boolean emailNotificationsEnabled;      // Email bildirimleri aktif mi?
    private Boolean smsNotificationsEnabled;        // SMS bildirimleri aktif mi?

    // Sistem Ayarları
    private String pharmacyName;                    // Eczane adı
    private String pharmacyPhone;                   // Eczane telefonu
    private String pharmacyEmail;                   // Eczane email
    private String pharmacyAddress;                 // Eczane adresi

    /**
     * Default constructor with system defaults
     */
    public SystemSettings() {
        // Varsayılan değerler
        this.medicationExpiryWarningDays = 3;
        this.prescriptionExpiryWarningDays = 7;
        this.stockExpiryWarningDays = 90;
        this.notificationTime = LocalTime.of(9, 0); // 09:00
        this.emailNotificationsEnabled = true;
        this.smsNotificationsEnabled = false;
    }

    /**
     * Validates settings
     */
    public boolean isValid() {
        return medicationExpiryWarningDays != null && medicationExpiryWarningDays > 0
                && prescriptionExpiryWarningDays != null && prescriptionExpiryWarningDays > 0
                && stockExpiryWarningDays != null && stockExpiryWarningDays > 0
                && notificationTime != null;
    }

    /**
     * Checks if notifications should be sent at current time
     */
    public boolean isNotificationTime() {
        if (notificationTime == null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        // 15 dakikalık tolerans ile kontrol
        return now.isAfter(notificationTime.minusMinutes(15))
                && now.isBefore(notificationTime.plusMinutes(15));
    }
}