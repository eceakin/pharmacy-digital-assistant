package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String batchNumber;
    private LocalDate expiryDate;
    private Integer quantity;
    private Integer initialQuantity;
    private Integer minimumStockLevel;
    private StockStatus status;
    private String storageLocation;
    private String notes;

    // Computed fields
    private Long daysUntilExpiry;
    private Double usagePercentage;
    private Boolean isBelowMinimum;
    private Boolean isExpired;
    private Boolean isNearExpiry;
    private Boolean isAvailableForSale;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
