package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreateProductRequest;
import com.pharmacy.assistant.application.dto.request.UpdateProductRequest;
import com.pharmacy.assistant.application.dto.response.ProductResponse;
import com.pharmacy.assistant.application.dto.response.ProductSummaryResponse;
import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;

import java.util.List;
import java.util.UUID;

public interface ManageProductUseCase {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(UUID id, UpdateProductRequest request);
    ProductResponse getProductById(UUID id);
    ProductResponse getProductByBarcode(String barcode);
    List<ProductResponse> getAllProducts();
    List<ProductSummaryResponse> getAllProductsSummary();
    List<ProductResponse> getActiveProducts();
    List<ProductResponse> getProductsByCategory(ProductCategory category);
    List<ProductResponse> getProductsByManufacturer(String manufacturer);
    List<ProductResponse> searchProducts(String searchTerm);
    void deleteProduct(UUID id);
    void activateProduct(UUID id);
    void deactivateProduct(UUID id);
    void discontinueProduct(UUID id);
    long getTotalProductCount();
    long getProductCountByStatus(ProductStatus status);
    long getProductCountByCategory(ProductCategory category);
}
