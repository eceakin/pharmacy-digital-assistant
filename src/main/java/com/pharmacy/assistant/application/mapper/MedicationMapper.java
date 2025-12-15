package com.pharmacy.assistant.application.mapper;

import com.pharmacy.assistant.application.dto.request.CreateMedicationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateMedicationRequest;
import com.pharmacy.assistant.application.dto.response.MedicationResponse;
import com.pharmacy.assistant.application.dto.response.MedicationSummaryResponse;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.valueobject.Dosage;
import com.pharmacy.assistant.domain.valueobject.MedicationSchedule;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public Medication toDomain(CreateMedicationRequest request) {
        Medication medication = new Medication();
        medication.setPatientId(request.getPatientId());
        medication.setProductId(request.getProductId());
        medication.setStatus(MedicationStatus.ACTIVE);

        // Dosage
        Dosage dosage = new Dosage(
                request.getDosageAmount(),
                request.getDosageUnit(),
                request.getDosageInstructions()
        );
        medication.setDosage(dosage);

        // Schedule
        MedicationSchedule schedule = new MedicationSchedule(
                request.getStartDate(),
                request.getEndDate(),
                request.getFrequency(),
                request.getTimings()
        );
        medication.setSchedule(schedule);

        // Other fields
        medication.setAdministrationRoute(request.getAdministrationRoute());
        medication.setIndication(request.getIndication());
        medication.setSideEffects(request.getSideEffects());
        medication.setContraindications(request.getContraindications());
        medication.setSpecialInstructions(request.getSpecialInstructions());
        medication.setNotes(request.getNotes());

        return medication;
    }

    public void updateDomainFromRequest(Medication medication, UpdateMedicationRequest request) {
        // Dosage
        Dosage dosage = new Dosage(
                request.getDosageAmount(),
                request.getDosageUnit(),
                request.getDosageInstructions()
        );
        medication.setDosage(dosage);

        // Schedule
        MedicationSchedule schedule = new MedicationSchedule(
                request.getStartDate(),
                request.getEndDate(),
                request.getFrequency(),
                request.getTimings()
        );
        medication.setSchedule(schedule);

        // Other fields
        medication.setAdministrationRoute(request.getAdministrationRoute());
        medication.setIndication(request.getIndication());
        medication.setSideEffects(request.getSideEffects());
        medication.setContraindications(request.getContraindications());
        medication.setSpecialInstructions(request.getSpecialInstructions());
        medication.setNotes(request.getNotes());
    }

    public MedicationResponse toResponse(Medication medication, String patientName, String medicationName) {
        MedicationResponse.MedicationResponseBuilder builder = MedicationResponse.builder()
                .id(medication.getId())
                .patientId(medication.getPatientId())
                .patientName(patientName)
                .productId(medication.getProductId())
                .medicationName(medicationName)
                .administrationRoute(medication.getAdministrationRoute())
                .status(medication.getStatus())
                .indication(medication.getIndication())
                .sideEffects(medication.getSideEffects())
                .contraindications(medication.getContraindications())
                .specialInstructions(medication.getSpecialInstructions())
                .notes(medication.getNotes())
                .createdAt(medication.getCreatedAt())
                .updatedAt(medication.getUpdatedAt())
                .isExpired(medication.isExpired())
                .needsRefill(medication.needsRefill());

        // Dosage
        if (medication.getDosage() != null) {
            builder.dosageAmount(medication.getDosage().getAmount())
                    .dosageUnit(medication.getDosage().getUnit())
                    .formattedDosage(medication.getDosage().getFormattedDosage())
                    .dosageInstructions(medication.getDosage().getInstructions());
        }

        // Schedule
        if (medication.getSchedule() != null) {
            builder.startDate(medication.getSchedule().getStartDate())
                    .endDate(medication.getSchedule().getEndDate())
                    .frequency(medication.getSchedule().getFrequency())
                    .timings(medication.getSchedule().getTimings())
                    .remainingDays(medication.getSchedule().getRemainingDays());
        }

        return builder.build();
    }

    public MedicationSummaryResponse toSummaryResponse(Medication medication, String patientName, String medicationName) {
        MedicationSummaryResponse.MedicationSummaryResponseBuilder builder = MedicationSummaryResponse.builder()
                .id(medication.getId())
                .patientName(patientName)
                .medicationName(medicationName)
                .status(medication.getStatus())
                .needsRefill(medication.needsRefill());

        if (medication.getDosage() != null) {
            builder.formattedDosage(medication.getDosage().getFormattedDosage());
        }

        if (medication.getSchedule() != null) {
            builder.frequency(medication.getSchedule().getFrequency())
                    .startDate(medication.getSchedule().getStartDate())
                    .endDate(medication.getSchedule().getEndDate())
                    .remainingDays(medication.getSchedule().getRemainingDays());
        }

        return builder.build();
    }
}