package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private String manufacturer;
    private ProductCategory category;
    private ProductStatus status;
    private PrescriptionRequirement prescriptionRequirement;

    // Identifier bilgileri
    private String barcode;
    private String qrCode;
    private String serialNumber;

    // Fiyatlandırma bilgileri
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal vatRate;
    private BigDecimal priceWithVat;
    private BigDecimal profitMargin;

    // Ürün detayları
    private String activeIngredient;
    private String dosageForm;
    private String strength;
    private Integer shelfLife;
    private String storageConditions;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}