package com.pharmacy.assistant.infrastructure.adapter.web.inventory;

import com.pharmacy.assistant.application.dto.request.CreateStockRequest;
import com.pharmacy.assistant.application.dto.request.StockAdjustmentRequest;
import com.pharmacy.assistant.application.dto.request.UpdateStockRequest;
import com.pharmacy.assistant.application.dto.response.StockResponse;
import com.pharmacy.assistant.application.dto.response.StockSummaryResponse;
import com.pharmacy.assistant.application.port.input.ManageStockUseCase;
import com.pharmacy.assistant.domain.enums.StockStatus;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse; // Varsayılan wrapper
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
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final ManageStockUseCase manageStockUseCase;

    // --- CRUD Operations ---

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createStock(@Valid @RequestBody CreateStockRequest request) {
        log.info("REST: Create stock request received for product: {}", request.getProductId());
        StockResponse response = manageStockUseCase.createStock(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stok kaydı başarıyla oluşturuldu", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStockRequest request) {
        log.info("REST: Update stock request received for ID: {}", id);
        StockResponse response = manageStockUseCase.updateStock(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stok kaydı güncellendi", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> getStockById(@PathVariable UUID id) {
        StockResponse response = manageStockUseCase.getStockById(id);
        return ResponseEntity.ok(ApiResponse.success("Stok bilgisi getirildi", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStocks() {
        List<StockResponse> responses = manageStockUseCase.getAllStocks();
        return ResponseEntity.ok(ApiResponse.success("Tüm stoklar getirildi", responses));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<StockSummaryResponse>>> getAllStocksSummary() {
        List<StockSummaryResponse> responses = manageStockUseCase.getAllStocksSummary();
        return ResponseEntity.ok(ApiResponse.success("Stok özetleri getirildi", responses));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStock(@PathVariable UUID id) {
        manageStockUseCase.deleteStock(id);
        return ResponseEntity.ok(ApiResponse.success("Stok kaydı silindi", null));
    }

    // --- Product Based Queries ---

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStocksByProductId(@PathVariable UUID productId) {
        List<StockResponse> responses = manageStockUseCase.getStocksByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Ürüne ait stoklar getirildi", responses));
    }

    @GetMapping("/product/{productId}/available")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAvailableStocksByProductId(@PathVariable UUID productId) {
        List<StockResponse> responses = manageStockUseCase.getAvailableStocksByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Kullanılabilir stoklar getirildi", responses));
    }

    // --- Alerts and Status ---

    @GetMapping("/alerts/low")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getLowStocks() {
        List<StockResponse> responses = manageStockUseCase.getLowStocks();
        return ResponseEntity.ok(ApiResponse.success("Düşük seviyeli stoklar getirildi", responses));
    }

    @GetMapping("/alerts/expiring")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getExpiringSoon(@RequestParam(defaultValue = "90") int days) {
        List<StockResponse> responses = manageStockUseCase.getExpiringSoon(days);
        return ResponseEntity.ok(ApiResponse.success("Süresi yaklaşan stoklar getirildi", responses));
    }

    @GetMapping("/alerts/expired")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getExpiredStocks() {
        List<StockResponse> responses = manageStockUseCase.getExpiredStocks();
        return ResponseEntity.ok(ApiResponse.success("Süresi dolmuş stoklar getirildi", responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStocksByStatus(@PathVariable StockStatus status) {
        List<StockResponse> responses = manageStockUseCase.getStocksByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Duruma göre stoklar getirildi", responses));
    }

    // --- Stock Movements / Adjustments ---

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<StockResponse>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        StockResponse response = manageStockUseCase.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stok hareketi işlendi", response));
    }

    @PatchMapping("/{id}/add")
    public ResponseEntity<ApiResponse<StockResponse>> addQuantity(
            @PathVariable UUID id,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        StockResponse response = manageStockUseCase.addQuantity(id, quantity, reason);
        return ResponseEntity.ok(ApiResponse.success("Stok miktarı artırıldı", response));
    }

    @PatchMapping("/{id}/deduct")
    public ResponseEntity<ApiResponse<StockResponse>> deductQuantity(
            @PathVariable UUID id,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        StockResponse response = manageStockUseCase.deductQuantity(id, quantity, reason);
        return ResponseEntity.ok(ApiResponse.success("Stok miktarı düşüldü", response));
    }

    // --- State Changes ---

    @PatchMapping("/{id}/mark-expired")
    public ResponseEntity<ApiResponse<Void>> markAsExpired(@PathVariable UUID id) {
        manageStockUseCase.markAsExpired(id);
        return ResponseEntity.ok(ApiResponse.success("Stok süresi dolmuş olarak işaretlendi", null));
    }

    @PatchMapping("/{id}/mark-damaged")
    public ResponseEntity<ApiResponse<Void>> markAsDamaged(@PathVariable UUID id) {
        manageStockUseCase.markAsDamaged(id);
        return ResponseEntity.ok(ApiResponse.success("Stok hasarlı olarak işaretlendi", null));
    }

    // --- Statistics ---

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalStockCount() {
        long count = manageStockUseCase.getTotalStockCount();
        return ResponseEntity.ok(ApiResponse.success("Toplam stok kalemi sayısı", count));
    }

    @GetMapping("/product/{productId}/count/total")
    public ResponseEntity<ApiResponse<Integer>> getTotalQuantityByProduct(@PathVariable UUID productId) {
        Integer total = manageStockUseCase.getTotalQuantityByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Ürüne ait toplam miktar", total));
    }
}