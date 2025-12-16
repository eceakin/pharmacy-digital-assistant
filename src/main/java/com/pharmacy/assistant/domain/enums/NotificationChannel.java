package com.pharmacy.assistant.domain.enums;

public enum NotificationChannel {
    SMS("SMS"),
    EMAIL("E-posta"),
    PUSH("Push Bildirim"),
    IN_APP("Uygulama İçi");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this channel requires external service
     */
    public boolean requiresExternalService() {
        return this == SMS || this == EMAIL || this == PUSH;
    }

    /**
     * Checks if this channel supports delivery tracking
     */
    public boolean supportsDeliveryTracking() {
        return this == SMS || this == PUSH;
    }

    /**
     * Checks if this channel supports read receipts
     */
    public boolean supportsReadReceipts() {
        return this == PUSH || this == IN_APP;
    }
}
