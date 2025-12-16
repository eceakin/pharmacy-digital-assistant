// NotificationStatus.java
package com.pharmacy.assistant.domain.enums;

public enum NotificationStatus {
    PENDING("Beklemede"),
    SCHEDULED("Planlandı"),
    SENT("Gönderildi"),
    DELIVERED("İletildi"),
    READ("Okundu"),
    FAILED("Başarısız"),
    CANCELLED("İptal Edildi");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
