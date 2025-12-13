package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;


import com.pharmacy.assistant.application.port.output.PatientRepository;
import com.pharmacy.assistant.domain.enums.PatientStatus;
import com.pharmacy.assistant.domain.model.patient.Patient;
import com.pharmacy.assistant.domain.valueobject.ContactInfo;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PatientEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.PatientJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpaRepository;

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = toEntity(patient);
        PatientEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Patient> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findByStatus(PatientStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> searchByName(String searchTerm) {
        return jpaRepository.searchByName(searchTerm).stream()
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

    // Mapping methods
    private PatientEntity toEntity(Patient patient) {
        PatientEntity entity = new PatientEntity();
        entity.setId(patient.getId());
        entity.setFirstName(patient.getFirstName());
        entity.setLastName(patient.getLastName());
        entity.setPhoneNumber(patient.getContactInfo().getPhoneNumber());
        entity.setEmail(patient.getContactInfo().getEmail());
        entity.setAddress(patient.getContactInfo().getAddress());
        entity.setSmsConsentGiven(patient.isSmsConsentGiven());
        entity.setStatus(patient.getStatus());
        entity.setNotes(patient.getNotes());
        entity.setCreatedAt(patient.getCreatedAt());
        entity.setUpdatedAt(patient.getUpdatedAt());
        return entity;
    }

    private Patient toDomain(PatientEntity entity) {
        Patient patient = new Patient();
        patient.setId(entity.getId());
        patient.setFirstName(entity.getFirstName());
        patient.setLastName(entity.getLastName());

        ContactInfo contactInfo = new ContactInfo(
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getAddress()
        );
        patient.setContactInfo(contactInfo);

        patient.setSmsConsentGiven(entity.isSmsConsentGiven());
        patient.setStatus(entity.getStatus());
        patient.setNotes(entity.getNotes());
        patient.setCreatedAt(entity.getCreatedAt());
        patient.setUpdatedAt(entity.getUpdatedAt());

        return patient;
    }}