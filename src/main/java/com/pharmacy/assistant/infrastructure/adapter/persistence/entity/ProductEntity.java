package com.pharmacy.assistant.infrastructure.adapter.persistence.entity;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionRequirement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "manufacturer", nullable = false, length = 200)
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "prescription_requirement", nullable = false, length = 50)
    private PrescriptionRequirement prescriptionRequirement;

    // Identifier bilgileri
    @Column(name = "barcode", unique = true, length = 100)
    private String barcode;

    @Column(name = "qr_code", length = 200)
    private String qrCode;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    // Fiyatlandırma bilgileri
    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;

    // Ürün detayları
    @Column(name = "active_ingredient", length = 200)
    private String activeIngredient;

    @Column(name = "dosage_form", length = 100)
    private String dosageForm;

    @Column(name = "strength", length = 50)
    private String strength;

    @Column(name = "shelf_life")
    private Integer shelfLife;

    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;

    @Column(name = "notes", length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}