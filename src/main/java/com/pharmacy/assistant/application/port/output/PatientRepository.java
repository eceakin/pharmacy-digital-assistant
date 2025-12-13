package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.model.patient.Patient;
import com.pharmacy.assistant.domain.enums.PatientStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    List<Patient> findAll();
    List<Patient> findByStatus(PatientStatus status);
    List<Patient> searchByName(String searchTerm);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    long count();
}
