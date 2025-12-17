package com.pharmacy.assistant.domain.enums;

public enum NotificationChannel {
    SMS("SMS"),
    EMAIL("E-posta"),
    SYSTEM("Sistem");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresExternalService() {
        return this == SMS || this == EMAIL;
    }

    public boolean isPatientChannel() {
        return this == SMS || this == EMAIL;
    }
}