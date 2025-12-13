package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.ProductRepository;
import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.valueobject.ProductIdentifier;
import com.pharmacy.assistant.domain.valueobject.ProductPricing;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.ProductEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        return jpaRepository.findByBarcode(barcode).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return jpaRepository.findByCategory(category).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByName(String searchTerm) {
        return jpaRepository.searchByName(searchTerm).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByManufacturer(String manufacturer) {
        return jpaRepository.findByManufacturer(manufacturer).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
    public boolean existsByBarcode(String barcode) {
        return jpaRepository.existsByBarcode(barcode);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStatus(ProductStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countByCategory(ProductCategory category) {
        return jpaRepository.countByCategory(category);
    }

    // Mapping methods
    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setManufacturer(product.getManufacturer());
        entity.setCategory(product.getCategory());
        entity.setStatus(product.getStatus());
        entity.setPrescriptionRequirement(product.getPrescriptionRequirement());

        // Identifier
        if (product.getIdentifier() != null) {
            entity.setBarcode(product.getIdentifier().getBarcode());
            entity.setQrCode(product.getIdentifier().getQrCode());
            entity.setSerialNumber(product.getIdentifier().getSerialNumber());
        }

        // Pricing
        if (product.getPricing() != null) {
            entity.setPurchasePrice(product.getPricing().getPurchasePrice());
            entity.setSellingPrice(product.getPricing().getSellingPrice());
            entity.setVatRate(product.getPricing().getVatRate());
        }

        // Details
        entity.setActiveIngredient(product.getActiveIngredient());
        entity.setDosageForm(product.getDosageForm());
        entity.setStrength(product.getStrength());
        entity.setShelfLife(product.getShelfLife());
        entity.setStorageConditions(product.getStorageConditions());
        entity.setNotes(product.getNotes());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());

        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setManufacturer(entity.getManufacturer());
        product.setCategory(entity.getCategory());
        product.setStatus(entity.getStatus());
        product.setPrescriptionRequirement(entity.getPrescriptionRequirement());

        // Identifier
        ProductIdentifier identifier = new ProductIdentifier(
                entity.getBarcode(),
                entity.getQrCode(),
                entity.getSerialNumber()
        );
        product.setIdentifier(identifier);

        // Pricing
        ProductPricing pricing = new ProductPricing(
                entity.getPurchasePrice(),
                entity.getSellingPrice(),
                entity.getVatRate()
        );
        product.setPricing(pricing);

        // Details
        product.setActiveIngredient(entity.getActiveIngredient());
        product.setDosageForm(entity.getDosageForm());
        product.setStrength(entity.getStrength());
        product.setShelfLife(entity.getShelfLife());
        product.setStorageConditions(entity.getStorageConditions());
        product.setNotes(entity.getNotes());
        product.setCreatedAt(entity.getCreatedAt());
        product.setUpdatedAt(entity.getUpdatedAt());

        return product;
    }
}
