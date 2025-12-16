package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockJpaRepository extends JpaRepository<StockEntity, UUID> {

    List<StockEntity> findByProductId(UUID productId);

    List<StockEntity> findByStatus(StockStatus status);

    Optional<StockEntity> findByProductIdAndBatchNumber(UUID productId, String batchNumber);

    boolean existsByProductIdAndBatchNumber(UUID productId, String batchNumber);

    List<StockEntity> findByStorageLocation(String storageLocation);

    // Miktarı minimum seviyenin altında olanlar
    @Query("SELECT s FROM StockEntity s WHERE s.quantity < s.minimumStockLevel AND s.quantity > 0")
    List<StockEntity> findBelowMinimumLevel();

    // SKT aralığına göre sorgulama
    List<StockEntity> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    // SKT yaklaşanlar (Bugün ile eşik tarih arası)
    @Query("SELECT s FROM StockEntity s WHERE s.expiryDate BETWEEN :today AND :thresholdDate AND s.quantity > 0")
    List<StockEntity> findExpiringSoon(@Param("today") LocalDate today, @Param("thresholdDate") LocalDate thresholdDate);

    // SKT geçmiş olanlar
    @Query("SELECT s FROM StockEntity s WHERE s.expiryDate < :today AND s.quantity > 0")
    List<StockEntity> findExpiredStocks(@Param("today") LocalDate today);

    // Ürüne ait toplam miktar (SUM)
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.productId = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") UUID productId);

    // Ürüne ait kullanılabilir (AVAILABLE) miktar
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.productId = :productId AND s.status = 'AVAILABLE'")
    Integer getAvailableQuantityByProductId(@Param("productId") UUID productId);

    // FEFO (First Expire First Out) için sıralı getirme
    @Query("SELECT s FROM StockEntity s WHERE s.productId = :productId AND s.status = 'AVAILABLE' ORDER BY s.expiryDate ASC")
    List<StockEntity> findAvailableStocksSortedByExpiry(@Param("productId") UUID productId);

    // İstatistikler
    long countByProductId(UUID productId);
    long countByStatus(StockStatus status);
}