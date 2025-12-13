package com.pharmacy.assistant.infrastructure.adapter.web.patient;

import com.pharmacy.assistant.application.dto.request.CreatePatientRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePatientRequest;
import com.pharmacy.assistant.application.dto.response.PatientResponse;
import com.pharmacy.assistant.application.port.input.ManagePatientUseCase;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final ManagePatientUseCase managePatientUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {

        log.info("REST: Creating patient - {} {}", request.getFirstName(), request.getLastName());
        PatientResponse response = managePatientUseCase.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hasta başarıyla oluşturuldu", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {
        log.info("REST: Fetching all patients");
        List<PatientResponse> patients = managePatientUseCase.getAllPatients();

        return ResponseEntity.ok(
                ApiResponse.success("Hastalar başarıyla getirildi", patients)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable UUID id) {
        log.info("REST: Fetching patient with ID: {}", id);
        PatientResponse response = managePatientUseCase.getPatientById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Hasta başarıyla getirildi", response)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getActivePatients() {
        log.info("REST: Fetching active patients");
        List<PatientResponse> patients = managePatientUseCase.getActivePatients();

        return ResponseEntity.ok(
                ApiResponse.success("Aktif hastalar başarıyla getirildi", patients)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> searchPatients(
            @RequestParam String q) {

        log.info("REST: Searching patients with term: {}", q);
        List<PatientResponse> patients = managePatientUseCase.searchPatients(q);

        return ResponseEntity.ok(
                ApiResponse.success("Arama sonuçları başarıyla getirildi", patients)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {

        log.info("REST: Updating patient with ID: {}", id);
        PatientResponse response = managePatientUseCase.updatePatient(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Hasta başarıyla güncellendi", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable UUID id) {
        log.info("REST: Deleting patient with ID: {}", id);

        managePatientUseCase.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("hasta bbaşarıyla silindii", null));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activatePatient(@PathVariable UUID id) {
        log.info("REST: Activating patient with ID: {}", id);
        managePatientUseCase.activatePatient(id);

        return ResponseEntity.ok(
                ApiResponse.success("Hasta aktifleştirildi", null)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivatePatient(@PathVariable UUID id) {
        log.info("REST: Deactivating patient with ID: {}", id);
        managePatientUseCase.deactivatePatient(id);

        return ResponseEntity.ok(
                ApiResponse.success("Hasta pasifleştirildi", null)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalPatientCount() {
        log.info("REST: Getting total patient count");
        long count = managePatientUseCase.getTotalPatientCount();

        return ResponseEntity.ok(
                ApiResponse.success("Toplam hasta sayısı", count)
        );
    }
}