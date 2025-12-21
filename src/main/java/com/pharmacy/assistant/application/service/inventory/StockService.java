package com.pharmacy.assistant.application.service.inventory;

import com.pharmacy.assistant.application.dto.request.CreateStockRequest;
import com.pharmacy.assistant.application.dto.request.StockAdjustmentRequest;
import com.pharmacy.assistant.application.dto.request.UpdateStockRequest;
import com.pharmacy.assistant.application.dto.response.StockResponse;
import com.pharmacy.assistant.application.dto.response.StockSummaryResponse;
import com.pharmacy.assistant.application.mapper.StockMapper;
import com.pharmacy.assistant.application.port.input.ManageStockUseCase;
import com.pharmacy.assistant.application.port.output.ProductRepository;
import com.pharmacy.assistant.application.port.output.StockRepository;
import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.domain.exception.DuplicateBatchException;
import com.pharmacy.assistant.domain.exception.InsufficientStockException;
import com.pharmacy.assistant.domain.exception.ProductNotFoundException;
import com.pharmacy.assistant.domain.exception.StockNotFoundException;
import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.model.inventory.Stock;
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
public class StockService implements ManageStockUseCase {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;
    @Override
    public StockResponse createStock(CreateStockRequest request) {
        log.info("Creating new stock for product: {}, batch: {}",
                request.getProductId(), request.getBatchNumber());

        // Ürün kontrolü
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + request.getProductId()));

        // Aynı parti numarasıyla stok var mı kontrolü
        if (stockRepository.existsByProductIdAndBatchNumber(
                request.getProductId(), request.getBatchNumber())) {
            throw new DuplicateBatchException(
                    "Bu ürün için aynı parti numarasıyla stok zaten mevcut: " + request.getBatchNumber());
        }

        Stock stock = stockMapper.toDomain(request);

        // --- EKLENEN KISIM BAŞLANGIÇ ---
        // Yeni oluşturulan stoğun miktarını kontrol et ve durumu ona göre ayarla
        if (stock.isOutOfStock()) {
            stock.setStatus(StockStatus.OUT_OF_STOCK);
        } else if (stock.isBelowMinimumLevel()) {
            stock.setStatus(StockStatus.LOW_STOCK);
        }
        // --- EKLENEN KISIM BİTİŞ ---

        stock.prePersist();

        Stock savedStock = stockRepository.save(stock);

        log.info("Stock created successfully with ID: {}", savedStock.getId());
        return stockMapper.toResponse(savedStock, product.getName());
    }
    @Override
    public StockResponse updateStock(UUID id, UpdateStockRequest request) {
        log.info("Updating stock with ID: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stockMapper.updateDomainFromRequest(stock, request);

        // --- EKLENEN KISIM: Güncelleme sonrası durum kontrolü ---
        if (stock.isOutOfStock()) {
            stock.setStatus(StockStatus.OUT_OF_STOCK);
        } else if (stock.isBelowMinimumLevel()) {
            stock.setStatus(StockStatus.LOW_STOCK);
        } else if (!stock.isExpired() && !StockStatus.RESERVED.equals(stock.getStatus()) && !StockStatus.DAMAGED.equals(stock.getStatus())) {
            // Eğer stok yeterliyse ve özel bir durumu (Rezerve/Hasarlı) yoksa AVAILABLE yap
            stock.setStatus(StockStatus.AVAILABLE);
        }
        stock.prePersist();

        Stock updatedStock = stockRepository.save(stock);

        Product product = productRepository.findById(updatedStock.getProductId()).orElse(null);

        log.info("Stock updated successfully: {}", id);
        return stockMapper.toResponse(
                updatedStock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockById(UUID id) {
        log.info("Fetching stock with ID: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        Product product = productRepository.findById(stock.getProductId()).orElse(null);

        return stockMapper.toResponse(
                stock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getAllStocks() {
        log.info("Fetching all stocks");

        return stockRepository.findAll().stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockSummaryResponse> getAllStocksSummary() {
        log.info("Fetching all stocks summary");

        return stockRepository.findAll().stream()
                .map(this::toSummaryResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStock(UUID id) {
        log.info("Deleting stock with ID: {}", id);

        if (!stockRepository.existsById(id)) {
            throw new StockNotFoundException("Stok bulunamadı: " + id);
        }

        stockRepository.deleteById(id);
        log.info("Stock deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByProductId(UUID productId) {
        log.info("Fetching stocks for product: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Ürün bulunamadı: " + productId);
        }

        return stockRepository.findByProductId(productId).stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getAvailableStocksByProductId(UUID productId) {
        log.info("Fetching available stocks for product: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Ürün bulunamadı: " + productId);
        }

        return stockRepository.findAvailableByProductId(productId).stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByStatus(StockStatus status) {
        log.info("Fetching stocks by status: {}", status);

        return stockRepository.findByStatus(status).stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockByProductAndBatch(UUID productId, String batchNumber) {
        log.info("Fetching stock for product: {} and batch: {}", productId, batchNumber);

        Stock stock = stockRepository.findByProductIdAndBatchNumber(productId, batchNumber)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stok bulunamadı - Ürün: " + productId + ", Parti: " + batchNumber));

        Product product = productRepository.findById(productId).orElse(null);

        return stockMapper.toResponse(
                stock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStocks() {
        log.info("Fetching low stocks");

        return stockRepository.findLowStocks().stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getOutOfStocks() {
        log.info("Fetching out of stocks");

        return stockRepository.findOutOfStocks().stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getExpiringSoon(int daysThreshold) {
        log.info("Fetching stocks expiring in {} days", daysThreshold);

        return stockRepository.findExpiringSoon(daysThreshold).stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getExpiredStocks() {
        log.info("Fetching expired stocks");

        return stockRepository.findExpiredStocks().stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getBelowMinimumLevel() {
        log.info("Fetching stocks below minimum level");

        return stockRepository.findBelowMinimumLevel().stream()
                .map(this::toResponseWithProduct)
                .collect(Collectors.toList());
    }

    @Override
    public StockResponse adjustStock(StockAdjustmentRequest request) {
        log.info("Adjusting stock: {} - Type: {}, Quantity: {}",
                request.getStockId(), request.getTransactionType(), request.getQuantity());

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + request.getStockId()));

        if (request.getTransactionType().isInbound()) {
            stock.addQuantity(request.getQuantity());
        } else if (request.getTransactionType().isOutbound()) {
            stock.deductQuantity(request.getQuantity());
        }

        stock.prePersist();
        Stock updatedStock = stockRepository.save(stock);

        Product product = productRepository.findById(updatedStock.getProductId()).orElse(null);

        log.info("Stock adjusted successfully: {}", request.getStockId());
        return stockMapper.toResponse(
                updatedStock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    public StockResponse addQuantity(UUID stockId, Integer quantity, String reason) {
        log.info("Adding quantity to stock: {} - Quantity: {}", stockId, quantity);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + stockId));

        stock.addQuantity(quantity);
        stock.prePersist();

        Stock updatedStock = stockRepository.save(stock);
        Product product = productRepository.findById(updatedStock.getProductId()).orElse(null);

        return stockMapper.toResponse(
                updatedStock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    public StockResponse deductQuantity(UUID stockId, Integer quantity, String reason) {
        log.info("Deducting quantity from stock: {} - Quantity: {}", stockId, quantity);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + stockId));

        stock.deductQuantity(quantity);
        stock.prePersist();

        Stock updatedStock = stockRepository.save(stock);
        Product product = productRepository.findById(updatedStock.getProductId()).orElse(null);

        return stockMapper.toResponse(
                updatedStock,
                product != null ? product.getName() : "Unknown"
        );
    }

    @Override
    public void markAsExpired(UUID id) {
        log.info("Marking stock as expired: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stock.markAsExpired();
        stockRepository.save(stock);

        log.info("Stock marked as expired: {}", id);
    }

    @Override
    public void markAsDamaged(UUID id) {
        log.info("Marking stock as damaged: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stock.markAsDamaged();
        stockRepository.save(stock);

        log.info("Stock marked as damaged: {}", id);
    }

    @Override
    public void markAsRecalled(UUID id) {
        log.info("Marking stock as recalled: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stock.markAsRecalled();
        stockRepository.save(stock);

        log.info("Stock marked as recalled: {}", id);
    }

    @Override
    public void markAsReserved(UUID id) {
        log.info("Marking stock as reserved: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stock.markAsReserved();
        stockRepository.save(stock);

        log.info("Stock marked as reserved: {}", id);
    }

    @Override
    public void releaseReservation(UUID id) {
        log.info("Releasing reservation for stock: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stok bulunamadı: " + id));

        stock.releaseReservation();
        stockRepository.save(stock);

        log.info("Reservation released for stock: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse findOptimalStockForSale(UUID productId, Integer requestedQuantity) {
        log.info("Finding optimal stock for sale - Product: {}, Quantity: {}",
                productId, requestedQuantity);

        // FEFO stratejisi: Önce süresi dolacak olan stoklardan
        List<Stock> availableStocks = stockRepository.findAvailableStocksSortedByExpiry(productId);

        for (Stock stock : availableStocks) {
            if (stock.getQuantity() >= requestedQuantity && stock.isAvailableForSale()) {
                Product product = productRepository.findById(productId).orElse(null);
                return stockMapper.toResponse(
                        stock,
                        product != null ? product.getName() : "Unknown"
                );
            }
        }

        throw new InsufficientStockException(
                "Yeterli stok bulunamadı - Ürün: " + productId + ", İstenen: " + requestedQuantity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(UUID productId, Integer requestedQuantity) {
        Integer availableQuantity = stockRepository.getAvailableQuantityByProductId(productId);
        return availableQuantity != null && availableQuantity >= requestedQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalStockCount() {
        return stockRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getStockCountByProduct(UUID productId) {
        return stockRepository.countByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getStockCountByStatus(StockStatus status) {
        return stockRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalQuantityByProduct(UUID productId) {
        return stockRepository.getTotalQuantityByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableQuantityByProduct(UUID productId) {
        return stockRepository.getAvailableQuantityByProductId(productId);
    }

    // Helper methods
    private StockResponse toResponseWithProduct(Stock stock) {
        Product product = productRepository.findById(stock.getProductId()).orElse(null);
        return stockMapper.toResponse(
                stock,
                product != null ? product.getName() : "Unknown"
        );
    }

    private StockSummaryResponse toSummaryResponseWithProduct(Stock stock) {
        Product product = productRepository.findById(stock.getProductId()).orElse(null);
        return stockMapper.toSummaryResponse(
                stock,
                product != null ? product.getName() : "Unknown"
        );
    }
}