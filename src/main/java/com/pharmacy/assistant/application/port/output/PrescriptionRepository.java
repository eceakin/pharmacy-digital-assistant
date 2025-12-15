package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.model.prescription.Prescription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(UUID id);
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    List<Prescription> findAll();
    List<Prescription> findByPatientId(UUID patientId);
    List<Prescription> findByStatus(PrescriptionStatus status);
    List<Prescription> findActiveByPatientId(UUID patientId);
    List<Prescription> findExpiringBetween(LocalDate startDate, LocalDate endDate);
    List<Prescription> findExpiringSoon(int daysThreshold);
    List<Prescription> findExpiredPrescriptions();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByPrescriptionNumber(String prescriptionNumber);
    long count();
    long countByPatientId(UUID patientId);
    long countByStatus(PrescriptionStatus status);
}