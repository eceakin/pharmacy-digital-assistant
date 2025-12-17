package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.MedicationRepository;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.valueobject.Dosage;
import com.pharmacy.assistant.domain.valueobject.MedicationSchedule;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.MedicationEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.MedicationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicationRepositoryAdapter implements MedicationRepository {

    private final MedicationJpaRepository jpaRepository;

    @Override
    public Medication save(Medication medication) {
        MedicationEntity entity = toEntity(medication);
        MedicationEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Medication> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Medication> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findActiveByPatientId(UUID patientId) {
        // Domain mantığına göre "Active" statüsündekileri getiriyoruz.
        return jpaRepository.findByPatientIdAndStatus(patientId, MedicationStatus.ACTIVE).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findByProductId(UUID productId) {
        return jpaRepository.findByProductId(productId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findByStatus(MedicationStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findExpiringBetween(LocalDate startDate, LocalDate endDate) {
        // Status parametresini ekleyerek çağırın
        return jpaRepository.findByStatusAndScheduleEndDateBetween(MedicationStatus.ACTIVE, startDate, endDate)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public List<Medication> findNeedingRefill() {
        // Domain kuralı: 7 gün veya daha az kalanlar.
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(7);

        return jpaRepository.findNeedingRefill(today, thresholdDate).stream()
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
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByPatientId(UUID patientId) {
        return jpaRepository.countByPatientId(patientId);
    }

    @Override
    public long countByStatus(MedicationStatus status) {
        return jpaRepository.countByStatus(status);
    }

    // --- MAPPING METHODS ---

    private MedicationEntity toEntity(Medication medication) {
        MedicationEntity entity = new MedicationEntity();
        entity.setId(medication.getId());
        entity.setPatientId(medication.getPatientId());
        entity.setProductId(medication.getProductId());
        entity.setMedicationName(medication.getMedicationName());
        entity.setStatus(medication.getStatus());
        entity.setAdministrationRoute(medication.getAdministrationRoute());
        entity.setIndication(medication.getIndication());
        entity.setSideEffects(medication.getSideEffects());
        entity.setContraindications(medication.getContraindications());
        entity.setSpecialInstructions(medication.getSpecialInstructions());
        entity.setNotes(medication.getNotes());
        entity.setCreatedAt(medication.getCreatedAt());
        entity.setUpdatedAt(medication.getUpdatedAt());

        // Flatten Dosage Value Object
        if (medication.getDosage() != null) {
            entity.setDosageAmount(medication.getDosage().getAmount());
            entity.setDosageUnit(medication.getDosage().getUnit());
            entity.setDosageInstructions(medication.getDosage().getInstructions());
        }

        // Flatten Schedule Value Object
        if (medication.getSchedule() != null) {
            entity.setScheduleStartDate(medication.getSchedule().getStartDate());
            entity.setScheduleEndDate(medication.getSchedule().getEndDate());
            entity.setScheduleFrequency(medication.getSchedule().getFrequency());
            // Listeyi kopyalayarak veriyoruz (Defensive copy)
            if (medication.getSchedule().getTimings() != null) {
                entity.setScheduleTimings(new ArrayList<>(medication.getSchedule().getTimings()));
            }
        }

        return entity;
    }

    private Medication toDomain(MedicationEntity entity) {
        Medication medication = new Medication();
        medication.setId(entity.getId());
        medication.setPatientId(entity.getPatientId());
        medication.setProductId(entity.getProductId());
        medication.setMedicationName(entity.getMedicationName());
        medication.setStatus(entity.getStatus());
        medication.setAdministrationRoute(entity.getAdministrationRoute());
        medication.setIndication(entity.getIndication());
        medication.setSideEffects(entity.getSideEffects());
        medication.setContraindications(entity.getContraindications());
        medication.setSpecialInstructions(entity.getSpecialInstructions());
        medication.setNotes(entity.getNotes());
        medication.setCreatedAt(entity.getCreatedAt());
        medication.setUpdatedAt(entity.getUpdatedAt());

        // Reconstruct Dosage Value Object
        Dosage dosage = new Dosage(
                entity.getDosageAmount(),
                entity.getDosageUnit(),
                entity.getDosageInstructions()
        );
        medication.setDosage(dosage);

        // Reconstruct Schedule Value Object
        MedicationSchedule schedule = new MedicationSchedule(
                entity.getScheduleStartDate(),
                entity.getScheduleEndDate(),
                entity.getScheduleFrequency(),
                entity.getScheduleTimings() != null ? new ArrayList<>(entity.getScheduleTimings()) : new ArrayList<>()
        );
        medication.setSchedule(schedule);

        return medication;
    }
}