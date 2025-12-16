package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.PrescriptionRepository;
import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.model.prescription.Prescription;
import com.pharmacy.assistant.domain.valueobject.PrescriptionValidity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.PrescriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrescriptionRepositoryAdapter implements PrescriptionRepository {

    private final PrescriptionJpaRepository jpaRepository;

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionEntity entity = toEntity(prescription);
        PrescriptionEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Prescription> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber) {
        return jpaRepository.findByPrescriptionNumber(prescriptionNumber).map(this::toDomain);
    }

    @Override
    public List<Prescription> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findByStatus(PrescriptionStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findActiveByPatientId(UUID patientId) {
        return jpaRepository.findByPatientIdAndStatus(patientId, PrescriptionStatus.ACTIVE).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByEndDateBetween(startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public List<Prescription> findExpiringSoon(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        return jpaRepository.findExpiringSoon(
                        PrescriptionStatus.ACTIVE, // ✅ Enum gönderiyoruz
                        today,
                        thresholdDate
                ).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prescription> findExpiredPrescriptions() {
        return jpaRepository.findExpiredPrescriptions(
                        LocalDate.now(),
                        PrescriptionStatus.EXPIRED // ✅ Enum gönderiyoruz
                ).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByPrescriptionNumber(String prescriptionNumber) {
        return jpaRepository.existsByPrescriptionNumber(prescriptionNumber);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByPatientId(UUID patientId) {
        return jpaRepository.countByPatientId(patientId);
    }

    @Override
    public long countByStatus(PrescriptionStatus status) {
        return jpaRepository.countByStatus(status);
    }

    // --- MAPPERS ---

    private PrescriptionEntity toEntity(Prescription domain) {
        PrescriptionEntity entity = new PrescriptionEntity();
        entity.setId(domain.getId());
        entity.setPatientId(domain.getPatientId());
        entity.setPrescriptionNumber(domain.getPrescriptionNumber());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());
        entity.setDoctorName(domain.getDoctorName());
        entity.setDoctorSpecialty(domain.getDoctorSpecialty());
        entity.setInstitution(domain.getInstitution());
        entity.setDiagnosis(domain.getDiagnosis());
        entity.setNotes(domain.getNotes());
        entity.setRefillCount(domain.getRefillCount());
        entity.setRefillsRemaining(domain.getRefillsRemaining());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Flatten Validity Value Object
        if (domain.getValidity() != null) {
            entity.setIssueDate(domain.getValidity().getIssueDate());
            entity.setStartDate(domain.getValidity().getStartDate());
            entity.setEndDate(domain.getValidity().getEndDate());
            entity.setValidityDays(domain.getValidity().getValidityDays());
        }

        return entity;
    }

    private Prescription toDomain(PrescriptionEntity entity) {
        Prescription domain = new Prescription();
        domain.setId(entity.getId());
        domain.setPatientId(entity.getPatientId());
        domain.setPrescriptionNumber(entity.getPrescriptionNumber());
        domain.setType(entity.getType());
        domain.setStatus(entity.getStatus());
        domain.setDoctorName(entity.getDoctorName());
        domain.setDoctorSpecialty(entity.getDoctorSpecialty());
        domain.setInstitution(entity.getInstitution());
        domain.setDiagnosis(entity.getDiagnosis());
        domain.setNotes(entity.getNotes());
        domain.setRefillCount(entity.getRefillCount());
        domain.setRefillsRemaining(entity.getRefillsRemaining());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        // Reconstruct Validity Value Object
        if (entity.getIssueDate() != null && entity.getStartDate() != null && entity.getEndDate() != null) {
            PrescriptionValidity validity = new PrescriptionValidity(
                    entity.getIssueDate(),
                    entity.getStartDate(),
                    entity.getEndDate(),
                    entity.getValidityDays()
            );
            domain.setValidity(validity);
        }

        return domain;
    }
}