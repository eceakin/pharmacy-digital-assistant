package com.pharmacy.assistant.infrastructure.adapter.web.prescription;

import com.pharmacy.assistant.application.dto.request.CreatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.response.PrescriptionResponse;
import com.pharmacy.assistant.application.dto.response.PrescriptionSummaryResponse;
import com.pharmacy.assistant.application.port.input.ManagePrescriptionUseCase;
import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
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
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final ManagePrescriptionUseCase managePrescriptionUseCase;

    // --- CRUD OPERASYONLARI ---

    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request) {

        log.info("REST: Creating prescription with number: {}", request.getPrescriptionNumber());
        PrescriptionResponse response = managePrescriptionUseCase.createPrescription(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reçete başarıyla oluşturuldu", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> updatePrescription(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePrescriptionRequest request) {

        log.info("REST: Updating prescription with ID: {}", id);
        PrescriptionResponse response = managePrescriptionUseCase.updatePrescription(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete başarıyla güncellendi", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(@PathVariable UUID id) {
        log.info("REST: Fetching prescription with ID: {}", id);
        PrescriptionResponse response = managePrescriptionUseCase.getPrescriptionById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete detayları getirildi", response)
        );
    }

    @GetMapping("/number/{prescriptionNumber}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionByNumber(
            @PathVariable String prescriptionNumber) {

        log.info("REST: Fetching prescription with number: {}", prescriptionNumber);
        PrescriptionResponse response = managePrescriptionUseCase.getPrescriptionByNumber(prescriptionNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete bulundu", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable UUID id) {
        log.info("REST: Deleting prescription with ID: {}", id);
        managePrescriptionUseCase.deletePrescription(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete başarıyla silindi", null)
        );
    }

    // --- LİSTELEME VE SORGULAMA ---

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getAllPrescriptions() {
        log.info("REST: Fetching all prescriptions");
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getAllPrescriptions();

        return ResponseEntity.ok(
                ApiResponse.success("Tüm reçeteler getirildi", responses)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<PrescriptionSummaryResponse>>> getAllPrescriptionsSummary() {
        log.info("REST: Fetching prescription summaries");
        List<PrescriptionSummaryResponse> responses = managePrescriptionUseCase.getAllPrescriptionsSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Reçete özet listesi getirildi", responses)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionsByPatient(
            @PathVariable UUID patientId) {

        log.info("REST: Fetching prescriptions for patient: {}", patientId);
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getPrescriptionsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait reçeteler getirildi", responses)
        );
    }

    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getActivePrescriptionsByPatient(
            @PathVariable UUID patientId) {

        log.info("REST: Fetching active prescriptions for patient: {}", patientId);
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getActivePrescriptionsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait aktif reçeteler getirildi", responses)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionsByStatus(
            @PathVariable PrescriptionStatus status) {

        log.info("REST: Fetching prescriptions with status: {}", status);
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getPrescriptionsByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success("Duruma göre reçeteler getirildi", responses)
        );
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getExpiringPrescriptions(
            @RequestParam(defaultValue = "7") int days) {

        log.info("REST: Fetching prescriptions expiring in {} days", days);
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getExpiringPrescriptions(days);

        return ResponseEntity.ok(
                ApiResponse.success("Süresi yaklaşan reçeteler getirildi", responses)
        );
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getExpiredPrescriptions() {
        log.info("REST: Fetching expired prescriptions");
        List<PrescriptionResponse> responses = managePrescriptionUseCase.getExpiredPrescriptions();

        return ResponseEntity.ok(
                ApiResponse.success("Süresi dolmuş reçeteler getirildi", responses)
        );
    }

    // --- DURUM DEĞİŞİKLİKLERİ VE AKSİYONLAR (PATCH) ---

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activatePrescription(@PathVariable UUID id) {
        log.info("REST: Activating prescription with ID: {}", id);
        managePrescriptionUseCase.activatePrescription(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete aktifleştirildi", null)
        );
    }

    @PatchMapping("/{id}/expire")
    public ResponseEntity<ApiResponse<Void>> expirePrescription(@PathVariable UUID id) {
        log.info("REST: Expiring prescription with ID: {}", id);
        managePrescriptionUseCase.expirePrescription(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete süresi doldu olarak işaretlendi", null)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPrescription(@PathVariable UUID id) {
        log.info("REST: Cancelling prescription with ID: {}", id);
        managePrescriptionUseCase.cancelPrescription(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete iptal edildi", null)
        );
    }

    @PatchMapping("/{id}/mark-used")
    public ResponseEntity<ApiResponse<Void>> markPrescriptionAsUsed(@PathVariable UUID id) {
        log.info("REST: Marking prescription as used with ID: {}", id);
        managePrescriptionUseCase.markPrescriptionAsUsed(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete kullanıldı olarak işaretlendi", null)
        );
    }

    @PatchMapping("/{id}/use-refill")
    public ResponseEntity<ApiResponse<Void>> useRefill(@PathVariable UUID id) {
        log.info("REST: Using refill for prescription with ID: {}", id);
        managePrescriptionUseCase.useRefill(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reçete tekrar kullanımı (refill) düşüldü", null)
        );
    }

    // --- İSTATİSTİKLER ---

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalPrescriptionCount() {
        return ResponseEntity.ok(
                ApiResponse.success("Toplam reçete sayısı", managePrescriptionUseCase.getTotalPrescriptionCount())
        );
    }
}