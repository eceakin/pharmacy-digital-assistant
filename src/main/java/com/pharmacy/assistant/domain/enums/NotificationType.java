package com.pharmacy.assistant.domain.enums;

public enum NotificationType {
    MEDICATION_REMINDER("İlaç Hatırlatma"),
    MEDICATION_REFILL("İlaç Yenileme"),
    MEDICATION_EXPIRY("İlaç Reçete Süresi Dolacak"),
    PRESCRIPTION_EXPIRY("Reçete Süresi Dolacak"),
    PRESCRIPTION_RENEWAL("Reçete Yenileme"),
    STOCK_LOW("Düşük Stok Uyarısı"),
    STOCK_EXPIRY("Stok SKT Uyarısı"),
    GENERAL("Genel Bilgilendirme");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isMedicationRelated() {
        return this == MEDICATION_REMINDER ||
                this == MEDICATION_REFILL ||
                this == MEDICATION_EXPIRY;
    }

    public boolean isPrescriptionRelated() {
        return this == PRESCRIPTION_EXPIRY ||
                this == PRESCRIPTION_RENEWAL;
    }

    public boolean isStockRelated() {
        return this == STOCK_LOW ||
                this == STOCK_EXPIRY;
    }
}