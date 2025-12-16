package com.pharmacy.assistant.domain.enums;

/**
 * Stock Transaction Type Enum
 */

public enum StockTransactionType {
    INITIAL_STOCK("İlk Stok Girişi"),
    PURCHASE("Satın Alma"),
    SALE("Satış"),
    RETURN("İade"),
    ADJUSTMENT("Düzeltme"),
    DAMAGE("Hasar/Fire"),
    EXPIRY("SKT Dolması"),
    TRANSFER_IN("Transfer Girişi"),
    TRANSFER_OUT("Transfer Çıkışı"),
    RECALL("Geri Çağırma");

    private final String displayName;

    StockTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isInbound() {
        return this == INITIAL_STOCK
                || this == PURCHASE
                || this == RETURN
                || this == TRANSFER_IN
                || this == ADJUSTMENT;
    }

    public boolean isOutbound() {
        return this == SALE
                || this == DAMAGE
                || this == EXPIRY
                || this == TRANSFER_OUT
                || this == RECALL;
    }
}
