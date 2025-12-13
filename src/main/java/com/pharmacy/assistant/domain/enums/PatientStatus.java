package com.pharmacy.assistant.domain.enums;

public enum PatientStatus {
    ACTIVE("Aktif"),
    INACTIVE("Pasif"),
    ARCHIVED("Arşivlenmiş");

    private final String displayName;

    PatientStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}