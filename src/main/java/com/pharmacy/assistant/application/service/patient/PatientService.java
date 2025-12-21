package com.pharmacy.assistant.application.service.patient;

import com.pharmacy.assistant.application.dto.request.CreatePatientRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePatientRequest;
import com.pharmacy.assistant.application.dto.response.PatientResponse;
import com.pharmacy.assistant.application.mapper.PatientMapper;
import com.pharmacy.assistant.application.port.input.ManagePatientUseCase;
import com.pharmacy.assistant.application.port.output.MedicationRepository;
import com.pharmacy.assistant.application.port.output.NotificationRepository;
import com.pharmacy.assistant.application.port.output.PatientRepository;
import com.pharmacy.assistant.domain.enums.PatientStatus;
import com.pharmacy.assistant.domain.exception.PatientNotFoundException;
import com.pharmacy.assistant.domain.model.notification.Notification;
import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientService implements ManagePatientUseCase {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final NotificationRepository notificationRepository; // YENİ EKLENECEK
    private final MedicationRepository medicationRepository; // Bunu inject edin
    @Override
    public PatientResponse createPatient(CreatePatientRequest request) {
        log.info("Creating new patient: {} {}", request.getFirstName(), request.getLastName());

        Patient patient = patientMapper.toDomain(request);
        patient.prePersist();

        Patient savedPatient = patientRepository.save(patient);

        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        return patientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse updatePatient(UUID id, UpdatePatientRequest request) {
        log.info("Updating patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + id));

        patientMapper.updateDomainFromRequest(patient, request);
        patient.prePersist(); // Update timestamp

        Patient updatedPatient = patientRepository.save(patient);

        log.info("Patient updated successfully: {}", id);
        return patientMapper.toResponse(updatedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(UUID id) {
        log.info("Fetching patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + id));

        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        log.info("Fetching all patients");

        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getActivePatients() {
        log.info("Fetching active patients");

        return patientRepository.findByStatus(PatientStatus.ACTIVE).stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String searchTerm) {
        log.info("Searching patients with term: {}", searchTerm);

        return patientRepository.searchByName(searchTerm).stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePatient(UUID id) {
        log.info("Deleting patient with ID: {}", id);

        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + id);
        }

        // 1. İlaçları Sil (Bunu zaten yapmıştık)
        List<Medication> medications = medicationRepository.findByPatientId(id);
        if (!medications.isEmpty()) {
            medicationRepository.deleteAll(medications);
        }

        // 2. YENİ: Bildirimleri Sil (Bunu ekliyoruz)
        List<Notification> notifications = notificationRepository.findByPatientId(id);
        if (!notifications.isEmpty()) {
            notificationRepository.deleteAll(notifications);
        }

        // 3. Hastayı Sil
        patientRepository.deleteById(id);
        log.info("Patient and associated data (medications, notifications) deleted successfully: {}", id);
    }

    // PatientService.java içindeki deletePatient metodunuz:

    @Override
    public void activatePatient(UUID id) {
        log.info("Activating patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + id));

        patient.activate();
        patientRepository.save(patient);

        log.info("Patient activated successfully: {}", id);
    }

    @Override
    public void deactivatePatient(UUID id) {
        log.info("Deactivating patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + id));

        patient.deactivate();
        patientRepository.save(patient);

        log.info("Patient deactivated successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPatientCount() {
        return patientRepository.count();
    }
}