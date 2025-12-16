package com.pharmacy.assistant.domain.model.inventory;

import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Stock Domain Model
 * Represents individual stock batches with expiration dates
 * Each product can have multiple stock entries with different batches
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Stock extends BaseEntity {

    private UUID productId;           // İlişkili ürün ID'si
    private String batchNumber;       // Parti/Lot numarası
    private LocalDate expiryDate;     // Son Kullanma Tarihi (SKT)
    private Integer quantity;         // Mevcut miktar
    private Integer initialQuantity;  // Başlangıç miktarı
    private Integer minimumStockLevel; // Minimum stok seviyesi (MSS)
    private StockStatus status;       // Stok durumu
    private String storageLocation;   // Depo/Raf konumu
    private String notes;             // Notlar

    /**
     * Checks if stock is below minimum level
     */
    public boolean isBelowMinimumLevel() {
        if (minimumStockLevel == null || quantity == null) {
            return false;
        }
        return quantity < minimumStockLevel;
    }

    /**
     * Checks if stock is completely depleted
     */
    public boolean isOutOfStock() {
        return quantity == null || quantity <= 0;
    }

    /**
     * Checks if expiry date is approaching (within threshold days)
     */
    public boolean isNearExpiry(int daysThreshold) {
        if (expiryDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
        return daysUntilExpiry > 0 && daysUntilExpiry <= daysThreshold;
    }

    /**
     * Checks if stock has expired
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Gets remaining days until expiry
     */
    public long getDaysUntilExpiry() {
        if (expiryDate == null) {
            return -1; // No expiry date set
        }
        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, expiryDate);
        return Math.max(days, 0); // Return 0 if already expired
    }

    /**
     * Checks if stock is available for sale
     */
    public boolean isAvailableForSale() {
        return StockStatus.AVAILABLE.equals(status)
                && !isExpired()
                && !isOutOfStock();
    }

    /**
     * Deducts quantity from stock
     */
    public void deductQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Çıkış miktarı negatif olamaz");
        }
        if (quantity < amount) {
            throw new IllegalStateException("Yetersiz stok: Mevcut " + quantity + ", İstenen " + amount);
        }
        this.quantity -= amount;

        // Update status based on quantity
        if (this.quantity == 0) {
            this.status = StockStatus.OUT_OF_STOCK;
        } else if (isBelowMinimumLevel()) {
            this.status = StockStatus.LOW_STOCK;
        }
    }

    /**
     * Adds quantity to stock
     */
    public void addQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Giriş miktarı negatif olamaz");
        }
        this.quantity += amount;

        // Update status
        if (this.quantity > 0 && StockStatus.OUT_OF_STOCK.equals(this.status)) {
            this.status = StockStatus.AVAILABLE;
        }
        if (!isBelowMinimumLevel() && StockStatus.LOW_STOCK.equals(this.status)) {
            this.status = StockStatus.AVAILABLE;
        }
    }

    /**
     * Marks stock as expired
     */
    public void markAsExpired() {
        this.status = StockStatus.EXPIRED;
    }

    /**
     * Marks stock as recalled
     */
    public void markAsRecalled() {
        this.status = StockStatus.RECALLED;
    }

    /**
     * Marks stock as damaged
     */
    public void markAsDamaged() {
        this.status = StockStatus.DAMAGED;
    }

    /**
     * Marks stock as reserved
     */
    public void markAsReserved() {
        if (isAvailableForSale()) {
            this.status = StockStatus.RESERVED;
        }
    }

    /**
     * Releases reserved stock back to available
     */
    public void releaseReservation() {
        if (StockStatus.RESERVED.equals(this.status)) {
            this.status = StockStatus.AVAILABLE;
        }
    }

    /**
     * Gets stock usage percentage
     */
    public double getUsagePercentage() {
        if (initialQuantity == null || initialQuantity == 0) {
            return 0.0;
        }
        int used = initialQuantity - (quantity != null ? quantity : 0);
        return (used * 100.0) / initialQuantity;
    }

    /**
     * Checks if this stock should be prioritized for sale (FEFO - First Expire First Out)
     */
    public boolean shouldBePrioritized(Stock other) {
        if (other == null || other.getExpiryDate() == null) {
            return true;
        }
        if (this.expiryDate == null) {
            return false;
        }
        return this.expiryDate.isBefore(other.getExpiryDate());
    }
}