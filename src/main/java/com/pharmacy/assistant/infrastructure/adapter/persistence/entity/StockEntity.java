package com.pharmacy.assistant.infrastructure.adapter.persistence.entity;

import com.pharmacy.assistant.domain.enums.StockStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stocks", indexes = {
        @Index(name = "idx_stock_product_id", columnList = "product_id"),
        @Index(name = "idx_stock_expiry_date", columnList = "expiry_date"),
        @Index(name = "idx_stock_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "batch_number", nullable = false, length = 50)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "minimum_stock_level")
    private Integer minimumStockLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StockStatus status;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}