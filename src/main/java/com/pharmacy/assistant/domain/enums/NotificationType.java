package com.pharmacy.assistant.domain.enums;

public enum NotificationType {
    MEDICATION_REMINDER("İlaç Hatırlatma"),
    MEDICATION_REFILL("İlaç Yenileme"),
    PRESCRIPTION_EXPIRY("Reçete Süresi Dolacak"),
    PRESCRIPTION_RENEWAL("Reçete Yenileme"),
    APPOINTMENT_REMINDER("Randevu Hatırlatma"),
    STOCK_ALERT("Stok Uyarısı"),
    PROMOTIONAL("Promosyon"),
    GENERAL("Genel Bilgilendirme");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this notification type requires patient consent
     */
    public boolean requiresConsent() {
        return this == PROMOTIONAL;
    }

    /**
     * Checks if this is a medication-related notification
     */
    public boolean isMedicationRelated() {
        return this == MEDICATION_REMINDER ||
                this == MEDICATION_REFILL;
    }

    /**
     * Checks if this is a prescription-related notification
     */
    public boolean isPrescriptionRelated() {
        return this == PRESCRIPTION_EXPIRY ||
                this == PRESCRIPTION_RENEWAL;
    }
}
