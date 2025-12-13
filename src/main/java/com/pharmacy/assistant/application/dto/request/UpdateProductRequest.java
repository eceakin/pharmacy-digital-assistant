package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.PrescriptionRequirement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "Ürün adı boş olamaz")
    private String name;

    private String description;

    @NotBlank(message = "Üretici firma adı boş olamaz")
    private String manufacturer;

    @NotNull(message = "Ürün kategorisi seçilmelidir")
    private ProductCategory category;

    @NotNull(message = "Reçete gereksinimi belirtilmelidir")
    private PrescriptionRequirement prescriptionRequirement;

    // Identifier bilgileri
    private String barcode;
    private String qrCode;
    private String serialNumber;

    // Fiyatlandırma bilgileri
    @DecimalMin(value = "0.0", inclusive = false, message = "Alış fiyatı sıfırdan büyük olmalıdır")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Satış fiyatı sıfırdan büyük olmalıdır")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", message = "KDV oranı sıfır veya pozitif olmalıdır")
    private BigDecimal vatRate;

    // Ürün detayları
    private String activeIngredient;
    private String dosageForm;
    private String strength;

    @Positive(message = "Raf ömrü pozitif bir sayı olmalıdır")
    private Integer shelfLife;

    private String storageConditions;
    private String notes;
}