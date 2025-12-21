package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.enums.MedicationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationRepository {
    Medication save(Medication medication);
    Optional<Medication> findById(UUID id);
    List<Medication> findAll();
    List<Medication> findByPatientId(UUID patientId);
    List<Medication> findByProductId(UUID productId);
    List<Medication> findByStatus(MedicationStatus status);
    List<Medication> findActiveByPatientId(UUID patientId);
    List<Medication> findExpiringBetween(LocalDate startDate, LocalDate endDate);
    List<Medication> findNeedingRefill();
    void deleteById(UUID id);
    void  deleteAll(List<Medication> medications);
    boolean existsById(UUID id);
    long count();
    long countByPatientId(UUID patientId);
    long countByStatus(MedicationStatus status);
}
