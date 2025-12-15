package com.pharmacy.assistant.domain.enums;

public enum PrescriptionStatus {
    ACTIVE("Aktif"),
    EXPIRED("Süresi Dolmuş"),
    USED("Kullanılmış"),
    CANCELLED("İptal Edilmiş"),
    PENDING("Beklemede");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}