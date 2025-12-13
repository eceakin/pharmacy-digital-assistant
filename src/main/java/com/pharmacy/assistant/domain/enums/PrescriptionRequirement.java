package com.pharmacy.assistant.domain.enums;

public enum PrescriptionRequirement {
    REQUIRED("Reçete Gerekli"),
    NOT_REQUIRED("Reçetesiz"),
    RED_PRESCRIPTION("Kırmızı Reçete"),
    GREEN_PRESCRIPTION("Yeşil Reçete"),
    PURPLE_PRESCRIPTION("Mor Reçete");

    private final String displayName;

    PrescriptionRequirement(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}