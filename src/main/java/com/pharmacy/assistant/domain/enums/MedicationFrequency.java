package com.pharmacy.assistant.domain.enums;


public enum MedicationFrequency {
    ONCE_DAILY("Günde 1 kez"),
    TWICE_DAILY("Günde 2 kez"),
    THREE_TIMES_DAILY("Günde 3 kez"),
    FOUR_TIMES_DAILY("Günde 4 kez"),
    EVERY_8_HOURS("Her 8 saatte bir"),
    EVERY_12_HOURS("Her 12 saatte bir"),
    EVERY_6_HOURS("Her 6 saatte bir"),
    AS_NEEDED("Gerektiğinde"),
    WEEKLY("Haftada 1 kez"),
    MONTHLY("Ayda 1 kez");

    private final String displayName;

    MedicationFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}