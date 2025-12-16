package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreateStockRequest;
import com.pharmacy.assistant.application.dto.request.UpdateStockRequest;
import com.pharmacy.assistant.application.dto.request.StockAdjustmentRequest;
import com.pharmacy.assistant.application.dto.response.StockResponse;
import com.pharmacy.assistant.application.dto.response.StockSummaryResponse;
import com.pharmacy.assistant.domain.enums.StockStatus;

import java.util.List;
import java.util.UUID;

/**
 * Use Case Port for Stock Management
 * Defines business operations for stock management
 */
public interface ManageStockUseCase {

    // CRUD Operations
    StockResponse createStock(CreateStockRequest request);
    StockResponse updateStock(UUID id, UpdateStockRequest request);
    StockResponse getStockById(UUID id);
    List<StockResponse> getAllStocks();
    List<StockSummaryResponse> getAllStocksSummary();
    void deleteStock(UUID id);

    // Query Operations
    List<StockResponse> getStocksByProductId(UUID productId);
    List<StockResponse> getAvailableStocksByProductId(UUID productId);
    List<StockResponse> getStocksByStatus(StockStatus status);
    StockResponse getStockByProductAndBatch(UUID productId, String batchNumber);

    // Stock Alerts
    List<StockResponse> getLowStocks();
    List<StockResponse> getOutOfStocks();
    List<StockResponse> getExpiringSoon(int daysThreshold);
    List<StockResponse> getExpiredStocks();
    List<StockResponse> getBelowMinimumLevel();

    // Stock Adjustments
    StockResponse adjustStock(StockAdjustmentRequest request);
    StockResponse addQuantity(UUID stockId, Integer quantity, String reason);
    StockResponse deductQuantity(UUID stockId, Integer quantity, String reason);

    // Status Changes
    void markAsExpired(UUID id);
    void markAsDamaged(UUID id);
    void markAsRecalled(UUID id);
    void markAsReserved(UUID id);
    void releaseReservation(UUID id);

    // Business Operations
    StockResponse findOptimalStockForSale(UUID productId, Integer requestedQuantity);
    boolean checkAvailability(UUID productId, Integer requestedQuantity);

    // Statistics
    long getTotalStockCount();
    long getStockCountByProduct(UUID productId);
    long getStockCountByStatus(StockStatus status);
    Integer getTotalQuantityByProduct(UUID productId);
    Integer getAvailableQuantityByProduct(UUID productId);
}