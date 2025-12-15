package com.pharmacy.assistant.domain.enums;

public enum PrescriptionType {
    E_PRESCRIPTION("E-Reçete"),
    PAPER_PRESCRIPTION("Kağıt Reçete"),
    REPORT("Rapor"),
    SPECIAL_PRESCRIPTION("Özel Reçete");

    private final String displayName;

    PrescriptionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}