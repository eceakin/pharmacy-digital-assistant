package com.pharmacy.assistant.application.mapper;


import com.pharmacy.assistant.application.dto.request.CreatePatientRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePatientRequest;
import com.pharmacy.assistant.application.dto.response.PatientResponse;
import com.pharmacy.assistant.domain.enums.PatientStatus;
import com.pharmacy.assistant.domain.model.patient.Patient;
import com.pharmacy.assistant.domain.valueobject.ContactInfo;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toDomain(CreatePatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setSmsConsentGiven(request.isSmsConsentGiven());
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setNotes(request.getNotes());

        ContactInfo contactInfo = new ContactInfo(
                request.getPhoneNumber(),
                request.getEmail(),
                request.getAddress()
        );
        patient.setContactInfo(contactInfo);

        return patient;
    }

    public void updateDomainFromRequest(Patient patient, UpdatePatientRequest request) {
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setNotes(request.getNotes());

        if (request.getSmsConsentGiven() != null) {
            patient.setSmsConsentGiven(request.getSmsConsentGiven());
        }

        ContactInfo contactInfo = new ContactInfo(
                request.getPhoneNumber(),
                request.getEmail(),
                request.getAddress()
        );
        patient.setContactInfo(contactInfo);
    }

    public PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .fullName(patient.getFullName())
                .phoneNumber(patient.getContactInfo().getPhoneNumber())
                .email(patient.getContactInfo().getEmail())
                .address(patient.getContactInfo().getAddress())
                .smsConsentGiven(patient.isSmsConsentGiven())
                .status(patient.getStatus())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
