package com.pharmacy.assistant.application.service.inventory;


import com.pharmacy.assistant.application.dto.request.CreateProductRequest;
import com.pharmacy.assistant.application.dto.request.UpdateProductRequest;
import com.pharmacy.assistant.application.dto.response.ProductResponse;
import com.pharmacy.assistant.application.dto.response.ProductSummaryResponse;
import com.pharmacy.assistant.application.mapper.ProductMapper;
import com.pharmacy.assistant.application.port.input.ManageProductUseCase;
import com.pharmacy.assistant.application.port.output.ProductRepository;
import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import com.pharmacy.assistant.domain.exception.DuplicateBarcodeException;
import com.pharmacy.assistant.domain.exception.ProductNotFoundException;
import com.pharmacy.assistant.domain.model.inventory.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements ManageProductUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        // Barkod kontrolü
        if (request.getBarcode() != null && !request.getBarcode().isEmpty()) {
            if (productRepository.existsByBarcode(request.getBarcode())) {
                throw new DuplicateBarcodeException("Bu barkod zaten kayıtlı: " + request.getBarcode());
            }
        }

        Product product = productMapper.toDomain(request);
        product.prePersist();

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + id));

        // Barkod kontrolü (eğer değiştiriliyorsa)
        if (request.getBarcode() != null && !request.getBarcode().isEmpty()) {
            if (!request.getBarcode().equals(product.getIdentifier().getBarcode())) {
                if (productRepository.existsByBarcode(request.getBarcode())) {
                    throw new DuplicateBarcodeException("Bu barkod zaten kayıtlı: " + request.getBarcode());
                }
            }
        }

        productMapper.updateDomainFromRequest(product, request);
        product.prePersist();

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        log.info("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + id));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByBarcode(String barcode) {
        log.info("Fetching product with barcode: {}", barcode);

        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı, Barkod: " + barcode));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");

        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getAllProductsSummary() {
        log.info("Fetching all products summary");

        return productRepository.findAll().stream()
                .map(productMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching active products");

        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        log.info("Fetching products by category: {}", category);

        return productRepository.findByCategory(category).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByManufacturer(String manufacturer) {
        log.info("Fetching products by manufacturer: {}", manufacturer);

        return productRepository.findByManufacturer(manufacturer).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String searchTerm) {
        log.info("Searching products with term: {}", searchTerm);

        return productRepository.searchByName(searchTerm).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Ürün bulunamadı: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted successfully: {}", id);
    }

    @Override
    public void activateProduct(UUID id) {
        log.info("Activating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + id));

        product.activate();
        productRepository.save(product);

        log.info("Product activated successfully: {}", id);
    }

    @Override
    public void deactivateProduct(UUID id) {
        log.info("Deactivating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + id));

        product.deactivate();
        productRepository.save(product);

        log.info("Product deactivated successfully: {}", id);
    }

    @Override
    public void discontinueProduct(UUID id) {
        log.info("Discontinuing product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı" + id));
        product.discontinue();
        productRepository.save(product);
        log.info("Product discontinued successfully: {}", id);

    }


    @Override
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountByStatus(ProductStatus status) {
        return productRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountByCategory(ProductCategory category) {
        return productRepository.countByCategory(category);
    }

}