package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.response.PrescriptionResponse;
import com.pharmacy.assistant.application.dto.response.PrescriptionSummaryResponse;
import com.pharmacy.assistant.domain.enums.PrescriptionStatus;

import java.util.List;
import java.util.UUID;

public interface ManagePrescriptionUseCase {
    PrescriptionResponse createPrescription(CreatePrescriptionRequest request);
    PrescriptionResponse updatePrescription(UUID id, UpdatePrescriptionRequest request);
    PrescriptionResponse getPrescriptionById(UUID id);
    PrescriptionResponse getPrescriptionByNumber(String prescriptionNumber);
    List<PrescriptionResponse> getAllPrescriptions();
    List<PrescriptionSummaryResponse> getAllPrescriptionsSummary();
    List<PrescriptionResponse> getPrescriptionsByPatientId(UUID patientId);
    List<PrescriptionResponse> getActivePrescriptionsByPatientId(UUID patientId);
    List<PrescriptionResponse> getPrescriptionsByStatus(PrescriptionStatus status);
    List<PrescriptionResponse> getExpiringPrescriptions(int daysAhead);
    List<PrescriptionResponse> getExpiredPrescriptions();
    void deletePrescription(UUID id);
    void activatePrescription(UUID id);
    void expirePrescription(UUID id);
    void cancelPrescription(UUID id);
    void markPrescriptionAsUsed(UUID id);
    void useRefill(UUID id);
    long getTotalPrescriptionCount();
    long getPrescriptionCountByPatient(UUID patientId);
    long getPrescriptionCountByStatus(PrescriptionStatus status);
}