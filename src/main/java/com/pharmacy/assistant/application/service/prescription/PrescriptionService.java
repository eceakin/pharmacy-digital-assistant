package com.pharmacy.assistant.application.service.prescription;

import com.pharmacy.assistant.application.dto.request.CreatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.request.UpdatePrescriptionRequest;
import com.pharmacy.assistant.application.dto.response.PrescriptionResponse;
import com.pharmacy.assistant.application.dto.response.PrescriptionSummaryResponse;
import com.pharmacy.assistant.application.mapper.PrescriptionMapper;
import com.pharmacy.assistant.application.port.input.ManagePrescriptionUseCase;
import com.pharmacy.assistant.application.port.output.PatientRepository;
import com.pharmacy.assistant.application.port.output.PrescriptionRepository;
import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.exception.DuplicatePrescriptionNumberException;
import com.pharmacy.assistant.domain.exception.InvalidPrescriptionException;
import com.pharmacy.assistant.domain.exception.PatientNotFoundException;
import com.pharmacy.assistant.domain.exception.PrescriptionNotFoundException;
import com.pharmacy.assistant.domain.model.patient.Patient;
import com.pharmacy.assistant.domain.model.prescription.Prescription;
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
public class PrescriptionService implements ManagePrescriptionUseCase {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Override
    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
        log.info("Creating new prescription: {}", request.getPrescriptionNumber());

        // Hasta kontrolü
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + request.getPatientId()));

        // Reçete numarası kontrolü
        if (prescriptionRepository.existsByPrescriptionNumber(request.getPrescriptionNumber())) {
            throw new DuplicatePrescriptionNumberException(
                    "Bu reçete numarası zaten kayıtlı: " + request.getPrescriptionNumber());
        }

        // Tarih kontrolü
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPrescriptionException("Bitiş tarihi başlangıç tarihinden önce olamaz");
        }

        if (request.getIssueDate().isAfter(request.getStartDate())) {
            throw new InvalidPrescriptionException("Başlangıç tarihi düzenlenme tarihinden önce olamaz");
        }

        Prescription prescription = prescriptionMapper.toDomain(request);
        prescription.prePersist();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription created successfully with ID: {}", savedPrescription.getId());
        return prescriptionMapper.toResponse(savedPrescription, patient.getFullName());
    }

    @Override
    public PrescriptionResponse updatePrescription(UUID id, UpdatePrescriptionRequest request) {
        log.info("Updating prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        // Tarih kontrolü
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPrescriptionException("Bitiş tarihi başlangıç tarihinden önce olamaz");
        }

        if (request.getIssueDate().isAfter(request.getStartDate())) {
            throw new InvalidPrescriptionException("Başlangıç tarihi düzenlenme tarihinden önce olamaz");
        }

        prescriptionMapper.updateDomainFromRequest(prescription, request);
        prescription.prePersist();

        Prescription updatedPrescription = prescriptionRepository.save(prescription);

        // İlişkili hasta bilgisini getir
        Patient patient = patientRepository.findById(updatedPrescription.getPatientId()).orElse(null);

        log.info("Prescription updated successfully: {}", id);
        return prescriptionMapper.toResponse(
                updatedPrescription,
                patient != null ? patient.getFullName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(UUID id) {
        log.info("Fetching prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        Patient patient = patientRepository.findById(prescription.getPatientId()).orElse(null);

        return prescriptionMapper.toResponse(
                prescription,
                patient != null ? patient.getFullName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByNumber(String prescriptionNumber) {
        log.info("Fetching prescription with number: {}", prescriptionNumber);

        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
                .orElseThrow(() -> new PrescriptionNotFoundException(
                        "Reçete bulunamadı, Numara: " + prescriptionNumber));

        Patient patient = patientRepository.findById(prescription.getPatientId()).orElse(null);

        return prescriptionMapper.toResponse(
                prescription,
                patient != null ? patient.getFullName() : "Unknown"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getAllPrescriptions() {
        log.info("Fetching all prescriptions");

        return prescriptionRepository.findAll().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionSummaryResponse> getAllPrescriptionsSummary() {
        log.info("Fetching all prescriptions summary");

        return prescriptionRepository.findAll().stream()
                .map(this::toSummaryResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatientId(UUID patientId) {
        log.info("Fetching prescriptions for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getActivePrescriptionsByPatientId(UUID patientId) {
        log.info("Fetching active prescriptions for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return prescriptionRepository.findActiveByPatientId(patientId).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByStatus(PrescriptionStatus status) {
        log.info("Fetching prescriptions by status: {}", status);

        return prescriptionRepository.findByStatus(status).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getExpiringPrescriptions(int daysAhead) {
        log.info("Fetching prescriptions expiring in {} days", daysAhead);

        return prescriptionRepository.findExpiringSoon(daysAhead).stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getExpiredPrescriptions() {
        log.info("Fetching expired prescriptions");

        return prescriptionRepository.findExpiredPrescriptions().stream()
                .map(this::toResponseWithPatient)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePrescription(UUID id) {
        log.info("Deleting prescription with ID: {}", id);

        if (!prescriptionRepository.existsById(id)) {
            throw new PrescriptionNotFoundException("Reçete bulunamadı: " + id);
        }

        prescriptionRepository.deleteById(id);
        log.info("Prescription deleted successfully: {}", id);
    }

    @Override
    public void activatePrescription(UUID id) {
        log.info("Activating prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        prescription.activate();
        prescriptionRepository.save(prescription);

        log.info("Prescription activated successfully: {}", id);
    }

    @Override
    public void expirePrescription(UUID id) {
        log.info("Expiring prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        prescription.expire();
        prescriptionRepository.save(prescription);

        log.info("Prescription expired successfully: {}", id);
    }

    @Override
    public void cancelPrescription(UUID id) {
        log.info("Cancelling prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        prescription.cancel();
        prescriptionRepository.save(prescription);

        log.info("Prescription cancelled successfully: {}", id);
    }

    @Override
    public void markPrescriptionAsUsed(UUID id) {
        log.info("Marking prescription as used with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        prescription.markAsUsed();
        prescriptionRepository.save(prescription);

        log.info("Prescription marked as used successfully: {}", id);
    }

    @Override
    public void useRefill(UUID id) {
        log.info("Using refill for prescription with ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Reçete bulunamadı: " + id));

        if (!prescription.canBeRefilled()) {
            throw new InvalidPrescriptionException("Bu reçete tekrar kullanılamaz");
        }

        prescription.useRefill();
        prescriptionRepository.save(prescription);

        log.info("Refill used successfully for prescription: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPrescriptionCount() {
        return prescriptionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getPrescriptionCountByPatient(UUID patientId) {
        return prescriptionRepository.countByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPrescriptionCountByStatus(PrescriptionStatus status) {
        return prescriptionRepository.countByStatus(status);
    }

    // Helper methods
    private PrescriptionResponse toResponseWithPatient(Prescription prescription) {
        Patient patient = patientRepository.findById(prescription.getPatientId()).orElse(null);
        return prescriptionMapper.toResponse(
                prescription,
                patient != null ? patient.getFullName() : "Unknown"
        );
    }

    private PrescriptionSummaryResponse toSummaryResponseWithPatient(Prescription prescription) {
        Patient patient = patientRepository.findById(prescription.getPatientId()).orElse(null);
        return prescriptionMapper.toSummaryResponse(
                prescription,
                patient != null ? patient.getFullName() : "Unknown"
        );
    }
}