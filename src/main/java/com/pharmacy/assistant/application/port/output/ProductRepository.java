package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findByBarcode(String barcode);
    List<Product> findAll();
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategory(ProductCategory category);
    List<Product> searchByName(String searchTerm);
    List<Product> findByManufacturer(String manufacturer);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByBarcode(String barcode);
    long count();
    long countByStatus(ProductStatus status);
    long countByCategory(ProductCategory category);
}