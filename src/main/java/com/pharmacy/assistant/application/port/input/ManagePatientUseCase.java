package com.pharmacy.assistant.application.port.input;

import com.pharmacy.assistant.application.dto.request.CreatePatientRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePatientRequest;
import com.pharmacy.assistant.application.dto.response.PatientResponse;

import java.util.List;
import java.util.UUID;

public interface ManagePatientUseCase {
    PatientResponse createPatient(CreatePatientRequest request);
    PatientResponse updatePatient(UUID id, UpdatePatientRequest request);
    PatientResponse getPatientById(UUID id);
    List<PatientResponse> getAllPatients();
    List<PatientResponse> getActivePatients();
    List<PatientResponse> searchPatients(String searchTerm);
    void deletePatient(UUID id);
    void activatePatient(UUID id);
    void deactivatePatient(UUID id);
    long getTotalPatientCount();
}