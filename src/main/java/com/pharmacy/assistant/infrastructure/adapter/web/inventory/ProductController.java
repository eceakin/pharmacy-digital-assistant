package com.pharmacy.assistant.infrastructure.adapter.web.inventory;

import com.pharmacy.assistant.application.dto.request.CreateProductRequest;
import com.pharmacy.assistant.application.dto.request.UpdateProductRequest;
import com.pharmacy.assistant.application.dto.response.ProductResponse;
import com.pharmacy.assistant.application.dto.response.ProductSummaryResponse;
import com.pharmacy.assistant.application.port.input.ManageProductUseCase;
import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ManageProductUseCase manageProductUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        log.info("REST: Creating product - {}", request.getName());
        ProductResponse response = manageProductUseCase.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ürün başarıyla oluşturuldu", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("REST: Fetching all products");
        List<ProductResponse> products = manageProductUseCase.getAllProducts();

        return ResponseEntity.ok(
                ApiResponse.success("Ürünler başarıyla getirildi", products)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getAllProductsSummary() {
        log.info("REST: Fetching all products summary");
        List<ProductSummaryResponse> products = manageProductUseCase.getAllProductsSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Ürün özeti başarıyla getirildi", products)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        log.info("REST: Fetching product with ID: {}", id);
        ProductResponse response = manageProductUseCase.getProductById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün başarıyla getirildi", response)
        );
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByBarcode(@PathVariable String barcode) {
        log.info("REST: Fetching product with barcode: {}", barcode);
        ProductResponse response = manageProductUseCase.getProductByBarcode(barcode);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün başarıyla getirildi", response)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts() {
        log.info("REST: Fetching active products");
        List<ProductResponse> products = manageProductUseCase.getActiveProducts();

        return ResponseEntity.ok(
                ApiResponse.success("Aktif ürünler başarıyla getirildi", products)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable ProductCategory category) {

        log.info("REST: Fetching products by category: {}", category);
        List<ProductResponse> products = manageProductUseCase.getProductsByCategory(category);

        return ResponseEntity.ok(
                ApiResponse.success("Kategoriye göre ürünler başarıyla getirildi", products)
        );
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByManufacturer(
            @PathVariable String manufacturer) {

        log.info("REST: Fetching products by manufacturer: {}", manufacturer);
        List<ProductResponse> products = manageProductUseCase.getProductsByManufacturer(manufacturer);

        return ResponseEntity.ok(
                ApiResponse.success("Üreticiye göre ürünler başarıyla getirildi", products)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String q) {

        log.info("REST: Searching products with term: {}", q);
        List<ProductResponse> products = manageProductUseCase.searchProducts(q);

        return ResponseEntity.ok(
                ApiResponse.success("Arama sonuçları başarıyla getirildi", products)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {

        log.info("REST: Updating product with ID: {}", id);
        ProductResponse response = manageProductUseCase.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün başarıyla güncellendi", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        log.info("REST: Deleting product with ID: {}", id);
        manageProductUseCase.deleteProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün başarıyla silindi", null)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateProduct(@PathVariable UUID id) {
        log.info("REST: Activating product with ID: {}", id);
        manageProductUseCase.activateProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün aktifleştirildi", null)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(@PathVariable UUID id) {
        log.info("REST: Deactivating product with ID: {}", id);
        manageProductUseCase.deactivateProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ürün pasifleştirildi", null)
        );
    }

    @PatchMapping("/{id}/discontinue")
    public ResponseEntity<ApiResponse<Void>> discontinueProduct(@PathVariable UUID id) {
        log.info("REST: Discontinuing product with ID: {}", id);
        manageProductUseCase.discontinueProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ürünün üretimi durduruldu olarak işaretlendi", null)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalProductCount() {
        log.info("REST: Getting total product count");
        long count = manageProductUseCase.getTotalProductCount();

        return ResponseEntity.ok(
                ApiResponse.success("Toplam ürün sayısı", count)
        );
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> getProductCountByStatus(@PathVariable ProductStatus status) {
        log.info("REST: Getting product count by status: {}", status);
        long count = manageProductUseCase.getProductCountByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success("Duruma göre ürün sayısı", count)
        );
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<ApiResponse<Long>> getProductCountByCategory(@PathVariable ProductCategory category) {
        log.info("REST: Getting product count by category: {}", category);
        long count = manageProductUseCase.getProductCountByCategory(category);

        return ResponseEntity.ok(
                ApiResponse.success("Kategoriye göre ürün sayısı", count)
        );
    }
}