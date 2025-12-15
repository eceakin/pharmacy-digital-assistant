package com.pharmacy.assistant.infrastructure.adapter.web.medication;

import com.pharmacy.assistant.application.dto.request.CreateMedicationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateMedicationRequest;
import com.pharmacy.assistant.application.dto.response.MedicationResponse;
import com.pharmacy.assistant.application.dto.response.MedicationSummaryResponse;
import com.pharmacy.assistant.application.port.input.ManageMedicationUseCase;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
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
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final ManageMedicationUseCase manageMedicationUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @Valid @RequestBody CreateMedicationRequest request) {

        log.info("REST: Creating medication for patient ID: {}", request.getPatientId());
        MedicationResponse response = manageMedicationUseCase.createMedication(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("İlaç takibi başarıyla oluşturuldu", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationResponse>> updateMedication(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMedicationRequest request) {

        log.info("REST: Updating medication with ID: {}", id);
        MedicationResponse response = manageMedicationUseCase.updateMedication(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("İlaç bilgileri başarıyla güncellendi", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationResponse>> getMedicationById(@PathVariable UUID id) {
        log.info("REST: Fetching medication with ID: {}", id);
        MedicationResponse response = manageMedicationUseCase.getMedicationById(id);

        return ResponseEntity.ok(
                ApiResponse.success("İlaç bilgisi getirildi", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getAllMedications() {
        log.info("REST: Fetching all medications");
        List<MedicationResponse> medications = manageMedicationUseCase.getAllMedications();

        return ResponseEntity.ok(
                ApiResponse.success("Tüm ilaçlar getirildi", medications)
        );
    }

    // Listeleme ekranları için daha hafif (summary) veri döner
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<MedicationSummaryResponse>>> getAllMedicationsSummary() {
        log.info("REST: Fetching medication summaries");
        List<MedicationSummaryResponse> summary = manageMedicationUseCase.getAllMedicationsSummary();

        return ResponseEntity.ok(
                ApiResponse.success("İlaç özet listesi getirildi", summary)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedicationsByPatient(@PathVariable UUID patientId) {
        log.info("REST: Fetching medications for patient: {}", patientId);
        List<MedicationResponse> medications = manageMedicationUseCase.getMedicationsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait ilaçlar getirildi", medications)
        );
    }

    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getActiveMedicationsByPatient(@PathVariable UUID patientId) {
        log.info("REST: Fetching active medications for patient: {}", patientId);
        List<MedicationResponse> medications = manageMedicationUseCase.getActiveMedicationsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Hastaya ait aktif ilaçlar getirildi", medications)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedicationsByStatus(@PathVariable MedicationStatus status) {
        log.info("REST: Fetching medications with status: {}", status);
        List<MedicationResponse> medications = manageMedicationUseCase.getMedicationsByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success("Duruma göre ilaçlar getirildi", medications)
        );
    }

    @GetMapping("/refill-needed")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedicationsNeedingRefill() {
        log.info("REST: Fetching medications needing refill");
        List<MedicationResponse> medications = manageMedicationUseCase.getMedicationsNeedingRefill();

        return ResponseEntity.ok(
                ApiResponse.success("Yenilenmesi gereken ilaçlar getirildi", medications)
        );
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getExpiringMedications(
            @RequestParam(defaultValue = "7") int days) {

        log.info("REST: Fetching medications expiring in {} days", days);
        List<MedicationResponse> medications = manageMedicationUseCase.getExpiringMedications(days);

        return ResponseEntity.ok(
                ApiResponse.success("Süresi dolmak üzere olan ilaçlar getirildi", medications)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedication(@PathVariable UUID id) {
        log.info("REST: Deleting medication with ID: {}", id);
        manageMedicationUseCase.deleteMedication(id);

        return ResponseEntity.ok(
                ApiResponse.success("İlaç kaydı başarıyla silindi", null)
        );
    }

    // --- State Change Endpoints (PATCH) ---

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateMedication(@PathVariable UUID id) {
        log.info("REST: Activating medication with ID: {}", id);
        manageMedicationUseCase.activateMedication(id);
        return ResponseEntity.ok(ApiResponse.success("İlaç aktifleştirildi", null));
    }

    @PatchMapping("/{id}/discontinue")
    public ResponseEntity<ApiResponse<Void>> discontinueMedication(@PathVariable UUID id) {
        log.info("REST: Discontinuing medication with ID: {}", id);
        manageMedicationUseCase.discontinueMedication(id);
        return ResponseEntity.ok(ApiResponse.success("İlaç kullanımı durduruldu", null));
    }

    @PatchMapping("/{id}/hold")
    public ResponseEntity<ApiResponse<Void>> putMedicationOnHold(@PathVariable UUID id) {
        log.info("REST: Putting medication on hold with ID: {}", id);
        manageMedicationUseCase.putMedicationOnHold(id);
        return ResponseEntity.ok(ApiResponse.success("İlaç kullanımı askıya alındı", null));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeMedication(@PathVariable UUID id) {
        log.info("REST: Completing medication with ID: {}", id);
        manageMedicationUseCase.completeMedication(id);
        return ResponseEntity.ok(ApiResponse.success("İlaç tedavisi tamamlandı", null));
    }
}