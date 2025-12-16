package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.model.inventory.Stock;
import com.pharmacy.assistant.domain.enums.StockStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Stock
 * Defines data access operations for Stock aggregate
 */
public interface StockRepository {

    // Basic CRUD
    Stock save(Stock stock);
    Optional<Stock> findById(UUID id);
    List<Stock> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    long count();

    // Query by Product
    List<Stock> findByProductId(UUID productId);
    List<Stock> findAvailableByProductId(UUID productId);

    // Query by Status
    List<Stock> findByStatus(StockStatus status);
    List<Stock> findAvailableStocks();
    List<Stock> findLowStocks();
    List<Stock> findOutOfStocks();

    // Query by Expiry Date
    List<Stock> findExpiringBetween(LocalDate startDate, LocalDate endDate);
    List<Stock> findExpiringSoon(int daysThreshold);
    List<Stock> findExpiredStocks();

    // Query by Batch
    Optional<Stock> findByProductIdAndBatchNumber(UUID productId, String batchNumber);
    boolean existsByProductIdAndBatchNumber(UUID productId, String batchNumber);

    // Advanced Queries
    List<Stock> findBelowMinimumLevel();
    List<Stock> findByStorageLocation(String storageLocation);

    // Statistics
    long countByProductId(UUID productId);
    long countByStatus(StockStatus status);
    Integer getTotalQuantityByProductId(UUID productId);
    Integer getAvailableQuantityByProductId(UUID productId);

    // FEFO (First Expire First Out) - Önce süresi dolacak olanları getir
    List<Stock> findAvailableStocksSortedByExpiry(UUID productId);
}