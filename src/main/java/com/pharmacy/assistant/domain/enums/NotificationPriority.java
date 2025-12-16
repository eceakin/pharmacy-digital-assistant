package com.pharmacy.assistant.domain.enums;

public enum NotificationPriority {
    LOW("Düşük", 3),
    NORMAL("Normal", 2),
    HIGH("Yüksek", 1),
    URGENT("Acil", 0);

    private final String displayName;
    private final int priority;

    NotificationPriority(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Lower number = higher priority
     */
    public boolean isHigherThan(NotificationPriority other) {
        return this.priority < other.priority;
    }
}
