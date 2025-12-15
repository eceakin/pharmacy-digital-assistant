package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreateMedicationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateMedicationRequest;
import com.pharmacy.assistant.application.dto.response.MedicationResponse;
import com.pharmacy.assistant.application.dto.response.MedicationSummaryResponse;
import com.pharmacy.assistant.domain.enums.MedicationStatus;

import java.util.List;
import java.util.UUID;

public interface ManageMedicationUseCase {
    MedicationResponse createMedication(CreateMedicationRequest request);
    MedicationResponse updateMedication(UUID id, UpdateMedicationRequest request);
    MedicationResponse getMedicationById(UUID id);
    List<MedicationResponse> getAllMedications();
    List<MedicationSummaryResponse> getAllMedicationsSummary();
    List<MedicationResponse> getMedicationsByPatientId(UUID patientId);
    List<MedicationResponse> getActiveMedicationsByPatientId(UUID patientId);
    List<MedicationResponse> getMedicationsByProductId(UUID productId);
    List<MedicationResponse> getMedicationsByStatus(MedicationStatus status);
    List<MedicationResponse> getExpiringMedications(int daysAhead);
    List<MedicationResponse> getMedicationsNeedingRefill();
    void deleteMedication(UUID id);
    void activateMedication(UUID id);
    void discontinueMedication(UUID id);
    void putMedicationOnHold(UUID id);
    void completeMedication(UUID id);
    long getTotalMedicationCount();
    long getMedicationCountByPatient(UUID patientId);
    long getMedicationCountByStatus(MedicationStatus status);
}
