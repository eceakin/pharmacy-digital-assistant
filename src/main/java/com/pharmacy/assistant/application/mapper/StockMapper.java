package com.pharmacy.assistant.application.mapper;

import com.pharmacy.assistant.application.dto.request.CreateStockRequest;
import com.pharmacy.assistant.application.dto.request.UpdateStockRequest;
import com.pharmacy.assistant.application.dto.response.StockResponse;
import com.pharmacy.assistant.application.dto.response.StockSummaryResponse;
import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.domain.model.inventory.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public Stock toDomain(CreateStockRequest request) {
        Stock stock = new Stock();
        stock.setProductId(request.getProductId());
        stock.setBatchNumber(request.getBatchNumber());
        stock.setExpiryDate(request.getExpiryDate());
        stock.setQuantity(request.getQuantity());
        stock.setInitialQuantity(request.getQuantity()); // İlk miktar = başlangıç miktarı
        stock.setMinimumStockLevel(request.getMinimumStockLevel());
        stock.setStorageLocation(request.getStorageLocation());
        stock.setNotes(request.getNotes());

        // Determine initial status
        if (stock.isExpired()) {
            stock.setStatus(StockStatus.EXPIRED);
        } else if (stock.isNearExpiry(90)) { // 3 ay
            stock.setStatus(StockStatus.NEAR_EXPIRY);
        } else if (stock.isBelowMinimumLevel()) {
            stock.setStatus(StockStatus.LOW_STOCK);
        } else if (stock.isOutOfStock()) {
            stock.setStatus(StockStatus.OUT_OF_STOCK);
        } else {
            stock.setStatus(StockStatus.AVAILABLE);
        }

        return stock;
    }

    public void updateDomainFromRequest(Stock stock, UpdateStockRequest request) {
        if (request.getBatchNumber() != null) {
            stock.setBatchNumber(request.getBatchNumber());
        }
        if (request.getExpiryDate() != null) {
            stock.setExpiryDate(request.getExpiryDate());
        }
        if (request.getQuantity() != null) {
            stock.setQuantity(request.getQuantity());
        }
        if (request.getMinimumStockLevel() != null) {
            stock.setMinimumStockLevel(request.getMinimumStockLevel());
        }
        if (request.getStorageLocation() != null) {
            stock.setStorageLocation(request.getStorageLocation());
        }
        if (request.getNotes() != null) {
            stock.setNotes(request.getNotes());
        }
    }

    public StockResponse toResponse(Stock stock, String productName) {
        return StockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .productName(productName)
                .batchNumber(stock.getBatchNumber())
                .expiryDate(stock.getExpiryDate())
                .quantity(stock.getQuantity())
                .initialQuantity(stock.getInitialQuantity())
                .minimumStockLevel(stock.getMinimumStockLevel())
                .status(stock.getStatus())
                .storageLocation(stock.getStorageLocation())
                .notes(stock.getNotes())
                .daysUntilExpiry(stock.getDaysUntilExpiry())
                .usagePercentage(stock.getUsagePercentage())
                .isBelowMinimum(stock.isBelowMinimumLevel())
                .isExpired(stock.isExpired())
                .isNearExpiry(stock.isNearExpiry(90))
                .isAvailableForSale(stock.isAvailableForSale())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    public StockSummaryResponse toSummaryResponse(Stock stock, String productName) {
        return StockSummaryResponse.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .productName(productName)
                .batchNumber(stock.getBatchNumber())
                .expiryDate(stock.getExpiryDate())
                .quantity(stock.getQuantity())
                .minimumStockLevel(stock.getMinimumStockLevel())
                .status(stock.getStatus())
                .daysUntilExpiry(stock.getDaysUntilExpiry())
                .isNearExpiry(stock.isNearExpiry(90))
                .build();
    }
}