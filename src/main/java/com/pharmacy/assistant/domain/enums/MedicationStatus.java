package com.pharmacy.assistant.domain.enums;


public enum MedicationStatus {
    ACTIVE("Aktif"),
    DISCONTINUED("Kesildi"),
    ON_HOLD("Askıda"),
    COMPLETED("Tamamlandı");

    private final String displayName;

    MedicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
