package com.pharmacy.assistant.application.mapper;

import com.pharmacy.assistant.application.dto.request.CreatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.response.PrescriptionResponse;
import com.pharmacy.assistant.application.dto.response.PrescriptionSummaryResponse;
import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.model.prescription.Prescription;
import com.pharmacy.assistant.domain.valueobject.PrescriptionValidity;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public Prescription toDomain(CreatePrescriptionRequest request) {
        Prescription prescription = new Prescription();
        prescription.setPatientId(request.getPatientId());
        prescription.setPrescriptionNumber(request.getPrescriptionNumber());
        prescription.setType(request.getType());
        prescription.setStatus(PrescriptionStatus.ACTIVE);

        // Validity
        PrescriptionValidity validity = new PrescriptionValidity(
                request.getIssueDate(),
                request.getStartDate(),
                request.getEndDate(),
                request.getValidityDays()
        );
        prescription.setValidity(validity);

        // Doctor info
        prescription.setDoctorName(request.getDoctorName());
        prescription.setDoctorSpecialty(request.getDoctorSpecialty());
        prescription.setInstitution(request.getInstitution());

        // Other fields
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setNotes(request.getNotes());
        prescription.setRefillCount(request.getRefillCount());
        prescription.setRefillsRemaining(request.getRefillCount()); // Başlangıçta aynı

        return prescription;
    }
    // ✅ EKLENECEK 1. METOD: Entity -> Domain (Okuma işlemi için)
    public Prescription toDomain(com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity entity) {
        if (entity == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.setId(entity.getId());
        prescription.setPatientId(entity.getPatientId());
        prescription.setPrescriptionNumber(entity.getPrescriptionNumber());
        prescription.setType(entity.getType());
        prescription.setStatus(entity.getStatus());

        // Validity (Value Object) oluşturma
        PrescriptionValidity validity = new PrescriptionValidity(
                entity.getIssueDate(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getValidityDays()
        );
        prescription.setValidity(validity);

        // Doktor bilgileri
        prescription.setDoctorName(entity.getDoctorName());
        prescription.setDoctorSpecialty(entity.getDoctorSpecialty());
        prescription.setInstitution(entity.getInstitution());

        // Diğer alanlar
        prescription.setDiagnosis(entity.getDiagnosis());
        prescription.setNotes(entity.getNotes());
        prescription.setRefillCount(entity.getRefillCount());
        prescription.setRefillsRemaining(entity.getRefillsRemaining());

        // Base entity alanları
        prescription.setCreatedAt(entity.getCreatedAt());
        prescription.setUpdatedAt(entity.getUpdatedAt());

        return prescription;
    }

    // ✅ EKLENECEK 2. METOD: Domain -> Entity (Kaydetme işlemi için)
    public com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity toEntity(Prescription domain) {
        if (domain == null) {
            return null;
        }

        com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity entity =
                new com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity();

        entity.setId(domain.getId());
        entity.setPatientId(domain.getPatientId());
        entity.setPrescriptionNumber(domain.getPrescriptionNumber());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());

        // Validity alanlarını Entity'ye düzleştirerek aktarıyoruz
        if (domain.getValidity() != null) {
            entity.setIssueDate(domain.getValidity().getIssueDate());
            entity.setStartDate(domain.getValidity().getStartDate());
            entity.setEndDate(domain.getValidity().getEndDate());
            entity.setValidityDays(domain.getValidity().getValidityDays());
        }

        entity.setDoctorName(domain.getDoctorName());
        entity.setDoctorSpecialty(domain.getDoctorSpecialty());
        entity.setInstitution(domain.getInstitution());
        entity.setDiagnosis(domain.getDiagnosis());
        entity.setNotes(domain.getNotes());
        entity.setRefillCount(domain.getRefillCount());
        entity.setRefillsRemaining(domain.getRefillsRemaining());

        return entity;
    }
    public void updateDomainFromRequest(Prescription prescription, UpdatePrescriptionRequest request) {
        prescription.setType(request.getType());

        // Validity
        PrescriptionValidity validity = new PrescriptionValidity(
                request.getIssueDate(),
                request.getStartDate(),
                request.getEndDate(),
                request.getValidityDays()
        );
        prescription.setValidity(validity);

        // Doctor info
        prescription.setDoctorName(request.getDoctorName());
        prescription.setDoctorSpecialty(request.getDoctorSpecialty());
        prescription.setInstitution(request.getInstitution());

        // Other fields
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setNotes(request.getNotes());

        // Refill count güncellenmişse, kalan sayıyı da güncelle
        if (request.getRefillCount() != null) {
            prescription.setRefillCount(request.getRefillCount());
            // Dikkat: Mevcut refillsRemaining'i korumak isteyebilirsiniz
            // Burada iş kuralına göre karar verin
        }
    }

    public PrescriptionResponse toResponse(Prescription prescription, String patientName) {
        PrescriptionResponse.PrescriptionResponseBuilder builder = PrescriptionResponse.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatientId())
                .patientName(patientName)
                .prescriptionNumber(prescription.getPrescriptionNumber())
                .type(prescription.getType())
                .status(prescription.getStatus())
                .doctorName(prescription.getDoctorName())
                .doctorSpecialty(prescription.getDoctorSpecialty())
                .institution(prescription.getInstitution())
                .diagnosis(prescription.getDiagnosis())
                .notes(prescription.getNotes())
                .refillCount(prescription.getRefillCount())
                .refillsRemaining(prescription.getRefillsRemaining())
                .canBeRefilled(prescription.canBeRefilled())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt());

        // Validity
        if (prescription.getValidity() != null) {
            builder.issueDate(prescription.getValidity().getIssueDate())
                    .startDate(prescription.getValidity().getStartDate())
                    .endDate(prescription.getValidity().getEndDate())
                    .validityDays(prescription.getValidity().getValidityDays())
                    .remainingDays(prescription.getValidity().getRemainingDays())
                    .isValid(prescription.getValidity().isValid())
                    .isExpired(prescription.getValidity().isExpired())
                    .isExpiringSoon(prescription.getValidity().isExpiringSoon(7)); // 7 gün threshold
        }

        return builder.build();
    }

    public PrescriptionSummaryResponse toSummaryResponse(Prescription prescription, String patientName) {
        PrescriptionSummaryResponse.PrescriptionSummaryResponseBuilder builder =
                PrescriptionSummaryResponse.builder()
                        .id(prescription.getId())
                        .patientName(patientName)
                        .prescriptionNumber(prescription.getPrescriptionNumber())
                        .type(prescription.getType())
                        .status(prescription.getStatus())
                        .refillsRemaining(prescription.getRefillsRemaining());

        if (prescription.getValidity() != null) {
            builder.endDate(prescription.getValidity().getEndDate())
                    .remainingDays(prescription.getValidity().getRemainingDays())
                    .isExpiringSoon(prescription.getValidity().isExpiringSoon(7));
        }

        return builder.build();
    }
}