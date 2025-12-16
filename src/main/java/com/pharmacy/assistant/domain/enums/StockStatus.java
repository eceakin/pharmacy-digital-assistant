package com.pharmacy.assistant.domain.enums;

/**
 * Stock Status Enum
 */
public enum StockStatus {
    AVAILABLE("Mevcut"),
    LOW_STOCK("Düşük Stok"),
    OUT_OF_STOCK("Stokta Yok"),
    EXPIRED("Süresi Dolmuş"),
    NEAR_EXPIRY("SKT Yaklaşıyor"),
    RESERVED("Rezerve"),
    DAMAGED("Hasarlı"),
    RECALLED("Geri Çağrılmış");

    private final String displayName;

    StockStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

