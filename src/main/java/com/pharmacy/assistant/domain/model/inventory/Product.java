package com.pharmacy.assistant.domain.model.inventory;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionRequirement;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import com.pharmacy.assistant.domain.valueobject.ProductIdentifier;
import com.pharmacy.assistant.domain.valueobject.ProductPricing;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    private String name;
    private String description;
    private String manufacturer;
    private ProductCategory category;
    private ProductStatus status;
    private PrescriptionRequirement prescriptionRequirement;
    private ProductIdentifier identifier;
    private ProductPricing pricing;
    private String activeIngredient;  // Etken madde
    private String dosageForm;        // Dozaj formu (tablet, şurup, kapsül, vb.)
    private String strength;          // Güç/Doz (örn: 500mg, 10ml)
    private Integer shelfLife;        // Raf ömrü (gün cinsinden)
    private String storageConditions; // Saklama koşulları
    private String notes;

    public boolean isActive() {
        return ProductStatus.ACTIVE.equals(status);
    }

    public boolean requiresPrescription() {
        return !PrescriptionRequirement.NOT_REQUIRED.equals(prescriptionRequirement);
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void markOutOfStock() {
        this.status = ProductStatus.OUT_OF_STOCK;
    }

    public boolean hasValidIdentifier() {
        return identifier != null &&
                (identifier.hasBarcode() || identifier.hasQrCode() || identifier.hasSerialNumber());
    }
}