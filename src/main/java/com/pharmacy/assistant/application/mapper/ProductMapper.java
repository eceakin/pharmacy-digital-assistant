package com.pharmacy.assistant.application.mapper;

import com.pharmacy.assistant.application.dto.request.CreateProductRequest;
import com.pharmacy.assistant.application.dto.request.UpdateProductRequest;
import com.pharmacy.assistant.application.dto.response.ProductResponse;
import com.pharmacy.assistant.application.dto.response.ProductSummaryResponse;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.valueobject.ProductIdentifier;
import com.pharmacy.assistant.domain.valueobject.ProductPricing;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setManufacturer(request.getManufacturer());
        product.setCategory(request.getCategory());
        product.setStatus(ProductStatus.ACTIVE);
        product.setPrescriptionRequirement(request.getPrescriptionRequirement());

        // Identifier
        ProductIdentifier identifier = new ProductIdentifier(
                request.getBarcode(),
                request.getQrCode(),
                request.getSerialNumber()
        );
        product.setIdentifier(identifier);

        // Pricing
        ProductPricing pricing = new ProductPricing(
                request.getPurchasePrice(),
                request.getSellingPrice(),
                request.getVatRate()
        );
        product.setPricing(pricing);

        // Details
        product.setActiveIngredient(request.getActiveIngredient());
        product.setDosageForm(request.getDosageForm());
        product.setStrength(request.getStrength());
        product.setShelfLife(request.getShelfLife());
        product.setStorageConditions(request.getStorageConditions());
        product.setNotes(request.getNotes());

        return product;
    }

    public void updateDomainFromRequest(Product product, UpdateProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setManufacturer(request.getManufacturer());
        product.setCategory(request.getCategory());
        product.setPrescriptionRequirement(request.getPrescriptionRequirement());

        // Identifier
        ProductIdentifier identifier = new ProductIdentifier(
                request.getBarcode(),
                request.getQrCode(),
                request.getSerialNumber()
        );
        product.setIdentifier(identifier);

        // Pricing
        if (request.getPurchasePrice() != null && request.getSellingPrice() != null) {
            ProductPricing pricing = new ProductPricing(
                    request.getPurchasePrice(),
                    request.getSellingPrice(),
                    request.getVatRate()
            );
            product.setPricing(pricing);
        }

        // Details
        product.setActiveIngredient(request.getActiveIngredient());
        product.setDosageForm(request.getDosageForm());
        product.setStrength(request.getStrength());
        product.setShelfLife(request.getShelfLife());
        product.setStorageConditions(request.getStorageConditions());
        product.setNotes(request.getNotes());
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .manufacturer(product.getManufacturer())
                .category(product.getCategory())
                .status(product.getStatus())
                .prescriptionRequirement(product.getPrescriptionRequirement())
                .activeIngredient(product.getActiveIngredient())
                .dosageForm(product.getDosageForm())
                .strength(product.getStrength())
                .shelfLife(product.getShelfLife())
                .storageConditions(product.getStorageConditions())
                .notes(product.getNotes())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt());

        // Identifier
        if (product.getIdentifier() != null) {
            builder.barcode(product.getIdentifier().getBarcode())
                    .qrCode(product.getIdentifier().getQrCode())
                    .serialNumber(product.getIdentifier().getSerialNumber());
        }

        // Pricing
        if (product.getPricing() != null) {
            builder.purchasePrice(product.getPricing().getPurchasePrice())
                    .sellingPrice(product.getPricing().getSellingPrice())
                    .vatRate(product.getPricing().getVatRate())
                    .priceWithVat(product.getPricing().getPriceWithVat())
                    .profitMargin(product.getPricing().getProfitMargin());
        }

        return builder.build();
    }

    public ProductSummaryResponse toSummaryResponse(Product product) {
        ProductSummaryResponse.ProductSummaryResponseBuilder builder = ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .manufacturer(product.getManufacturer())
                .category(product.getCategory())
                .status(product.getStatus());

        if (product.getIdentifier() != null) {
            builder.barcode(product.getIdentifier().getBarcode());
        }

        if (product.getPricing() != null) {
            builder.sellingPrice(product.getPricing().getSellingPrice())
                    .priceWithVat(product.getPricing().getPriceWithVat());
        }

        return builder.build();
    }
}