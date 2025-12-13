package com.pharmacy.assistant.domain.enums;

public enum ProductStatus {
    ACTIVE("Aktif"),
    INACTIVE("Pasif"),
    DISCONTINUED("Üretimi Durduruldu"),
    OUT_OF_STOCK("Stokta Yok");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
