package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.StockRepository;
import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.domain.model.inventory.Stock;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.StockEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockRepositoryAdapter implements StockRepository {

    private final StockJpaRepository jpaRepository;

    @Override
    public Stock save(Stock stock) {
        StockEntity entity = toEntity(stock);
        StockEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Stock> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Stock> findByProductId(UUID productId) {
        return jpaRepository.findByProductId(productId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findAvailableByProductId(UUID productId) {
        // AVAILABLE olanları stream ile filtreleyebiliriz veya JPA metodunu çağırabiliriz
        return jpaRepository.findByProductId(productId).stream()
                .filter(entity -> StockStatus.AVAILABLE.equals(entity.getStatus()))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Stock> findByStatus(StockStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findAvailableStocks() {
        return jpaRepository.findByStatus(StockStatus.AVAILABLE).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findLowStocks() {
        // Domain mantığına göre LOW_STOCK statüsü veya miktar kontrolü
        return jpaRepository.findByStatus(StockStatus.LOW_STOCK).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findOutOfStocks() {
        return jpaRepository.findByStatus(StockStatus.OUT_OF_STOCK).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByExpiryDateBetween(startDate, endDate).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findExpiringSoon(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        return jpaRepository.findExpiringSoon(today, thresholdDate).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findExpiredStocks() {
        return jpaRepository.findExpiredStocks(LocalDate.now()).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Stock> findByProductIdAndBatchNumber(UUID productId, String batchNumber) {
        return jpaRepository.findByProductIdAndBatchNumber(productId, batchNumber).map(this::toDomain);
    }

    @Override
    public boolean existsByProductIdAndBatchNumber(UUID productId, String batchNumber) {
        return jpaRepository.existsByProductIdAndBatchNumber(productId, batchNumber);
    }

    @Override
    public List<Stock> findBelowMinimumLevel() {
        return jpaRepository.findBelowMinimumLevel().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Stock> findByStorageLocation(String storageLocation) {
        return jpaRepository.findByStorageLocation(storageLocation).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByProductId(UUID productId) {
        return jpaRepository.countByProductId(productId);
    }

    @Override
    public long countByStatus(StockStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public Integer getTotalQuantityByProductId(UUID productId) {
        Integer total = jpaRepository.getTotalQuantityByProductId(productId);
        return total != null ? total : 0;
    }

    @Override
    public Integer getAvailableQuantityByProductId(UUID productId) {
        Integer total = jpaRepository.getAvailableQuantityByProductId(productId);
        return total != null ? total : 0;
    }

    @Override
    public List<Stock> findAvailableStocksSortedByExpiry(UUID productId) {
        return jpaRepository.findAvailableStocksSortedByExpiry(productId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // --- MAPPERS ---

    private StockEntity toEntity(Stock stock) {
        StockEntity entity = new StockEntity();
        entity.setId(stock.getId());
        entity.setProductId(stock.getProductId());
        entity.setBatchNumber(stock.getBatchNumber());
        entity.setExpiryDate(stock.getExpiryDate());
        entity.setQuantity(stock.getQuantity());
        entity.setInitialQuantity(stock.getInitialQuantity());
        entity.setMinimumStockLevel(stock.getMinimumStockLevel());
        entity.setStatus(stock.getStatus());
        entity.setStorageLocation(stock.getStorageLocation());
        entity.setNotes(stock.getNotes());
        entity.setCreatedAt(stock.getCreatedAt());
        entity.setUpdatedAt(stock.getUpdatedAt());
        return entity;
    }

    private Stock toDomain(StockEntity entity) {
        Stock stock = new Stock();
        stock.setId(entity.getId());
        stock.setProductId(entity.getProductId());
        stock.setBatchNumber(entity.getBatchNumber());
        stock.setExpiryDate(entity.getExpiryDate());
        stock.setQuantity(entity.getQuantity());
        stock.setInitialQuantity(entity.getInitialQuantity());
        stock.setMinimumStockLevel(entity.getMinimumStockLevel());
        stock.setStatus(entity.getStatus());
        stock.setStorageLocation(entity.getStorageLocation());
        stock.setNotes(entity.getNotes());
        stock.setCreatedAt(entity.getCreatedAt());
        stock.setUpdatedAt(entity.getUpdatedAt());
        return stock;
    }
}