package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findByBarcode(String barcode);

    List<ProductEntity> findByStatus(ProductStatus status);

    List<ProductEntity> findByCategory(ProductCategory category);

    List<ProductEntity> findByManufacturer(String manufacturer);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.activeIngredient) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductEntity> searchByName(@Param("searchTerm") String searchTerm);

    boolean existsByBarcode(String barcode);

    long countByStatus(ProductStatus status);

    long countByCategory(ProductCategory category);
}