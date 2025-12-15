package com.pharmacy.assistant.application.service.patient;

import com.pharmacy.assistant.application.dto.request.CreateMedicationRequest;
import com.pharmacy.assistant.application.dto.request.UpdateMedicationRequest;
import com.pharmacy.assistant.application.dto.response.MedicationResponse;
import com.pharmacy.assistant.application.dto.response.MedicationSummaryResponse;
import com.pharmacy.assistant.application.mapper.MedicationMapper;
import com.pharmacy.assistant.application.port.input.ManageMedicationUseCase;
import com.pharmacy.assistant.application.port.output.MedicationRepository;
import com.pharmacy.assistant.application.port.output.PatientRepository;
import com.pharmacy.assistant.application.port.output.ProductRepository;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import com.pharmacy.assistant.domain.exception.InvalidMedicationException;
import com.pharmacy.assistant.domain.exception.MedicationNotFoundException;
import com.pharmacy.assistant.domain.exception.PatientNotFoundException;
import com.pharmacy.assistant.domain.exception.ProductNotFoundException;
import com.pharmacy.assistant.domain.model.inventory.Product;
import com.pharmacy.assistant.domain.model.patient.Medication;
import com.pharmacy.assistant.domain.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicationService implements ManageMedicationUseCase {

    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final ProductRepository productRepository;
    private final MedicationMapper medicationMapper;

    @Override
    public MedicationResponse createMedication(CreateMedicationRequest request) {
        log.info("Creating new medication for patient: {}", request.getPatientId());

        // Hasta kontrolü
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Hasta bulunamadı: " + request.getPatientId()));

        // Ürün kontrolü
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı: " + request.getProductId()));

        // Tarih kontrolü
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidMedicationException("Bitiş tarihi başlangıç tarihinden önce olamaz");
        }

        Medication medication = medicationMapper.toDomain(request);
        medication.setMedicationName(product.getName()); // Ürün adını set et
        medication.prePersist();

        Medication savedMedication = medicationRepository.save(medication);

        log.info("Medication created successfully with ID: {}", savedMedication.getId());
        return medicationMapper.toResponse(savedMedication, patient.getFullName(), product.getName());
    }

    @Override
    public MedicationResponse updateMedication(UUID id, UpdateMedicationRequest request) {
        log.info("Updating medication with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        // Tarih kontrolü
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidMedicationException("Bitiş tarihi başlangıç tarihinden önce olamaz");
        }

        medicationMapper.updateDomainFromRequest(medication, request);
        medication.prePersist();

        Medication updatedMedication = medicationRepository.save(medication);

        // İlişkili verileri getir
        Patient patient = patientRepository.findById(updatedMedication.getPatientId()).orElse(null);
        Product product = productRepository.findById(updatedMedication.getProductId()).orElse(null);

        log.info("Medication updated successfully: {}", id);
        return medicationMapper.toResponse(
                updatedMedication,
                patient != null ? patient.getFullName() : "Unknown",
                product != null ? product.getName() : updatedMedication.getMedicationName()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MedicationResponse getMedicationById(UUID id) {
        log.info("Fetching medication with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        Patient patient = patientRepository.findById(medication.getPatientId()).orElse(null);
        Product product = productRepository.findById(medication.getProductId()).orElse(null);

        return medicationMapper.toResponse(
                medication,
                patient != null ? patient.getFullName() : "Unknown",
                product != null ? product.getName() : medication.getMedicationName()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getAllMedications() {
        log.info("Fetching all medications");

        return medicationRepository.findAll().stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationSummaryResponse> getAllMedicationsSummary() {
        log.info("Fetching all medications summary");

        return medicationRepository.findAll().stream()
                .map(this::toSummaryResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedicationsByPatientId(UUID patientId) {
        log.info("Fetching medications for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return medicationRepository.findByPatientId(patientId).stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getActiveMedicationsByPatientId(UUID patientId) {
            log.info("Fetching active medications for patient: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Hasta bulunamadı: " + patientId);
        }

        return medicationRepository.findActiveByPatientId(patientId).stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedicationsByProductId(UUID productId) {
        log.info("Fetching medications for product: {}", productId);

        return medicationRepository.findByProductId(productId).stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedicationsByStatus(MedicationStatus status) {
        log.info("Fetching medications by status: {}", status);

        return medicationRepository.findByStatus(status).stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getExpiringMedications(int daysAhead) {
        log.info("Fetching medications expiring in {} days", daysAhead);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        return medicationRepository.findExpiringBetween(startDate, endDate).stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedicationsNeedingRefill() {
        log.info("Fetching medications needing refill");

        return medicationRepository.findNeedingRefill().stream()
                .map(this::toResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMedication(UUID id) {
        log.info("Deleting medication with ID: {}", id);

        if (!medicationRepository.existsById(id)) {
            throw new MedicationNotFoundException("İlaç bulunamadı: " + id);
        }

        medicationRepository.deleteById(id);
        log.info("Medication deleted successfully: {}", id);
    }

    @Override
    public void activateMedication(UUID id) {
        log.info("Activating medication with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        medication.activate();
        medicationRepository.save(medication);

        log.info("Medication activated successfully: {}", id);
    }

    @Override
    public void discontinueMedication(UUID id) {
        log.info("Discontinuing medication with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        medication.discontinue();
        medicationRepository.save(medication);

        log.info("Medication discontinued successfully: {}", id);
    }

    @Override
    public void putMedicationOnHold(UUID id) {
        log.info("Putting medication on hold with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        medication.putOnHold();
        medicationRepository.save(medication);

        log.info("Medication put on hold successfully: {}", id);
    }

    @Override
    public void completeMedication(UUID id) {
        log.info("Completing medication with ID: {}", id);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("İlaç bulunamadı: " + id));

        medication.complete();
        medicationRepository.save(medication);

        log.info("Medication completed successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalMedicationCount() {
        return medicationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getMedicationCountByPatient(UUID patientId) {
        return medicationRepository.countByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMedicationCountByStatus(MedicationStatus status) {
        return medicationRepository.countByStatus(status);
    }

    // Helper methods
    private MedicationResponse toResponseWithRelations(Medication medication) {
        Patient patient = patientRepository.findById(medication.getPatientId()).orElse(null);
        Product product = productRepository.findById(medication.getProductId()).orElse(null);

        return medicationMapper.toResponse(
                medication,
                patient != null ? patient.getFullName() : "Unknown",
                product != null ? product.getName() : medication.getMedicationName()
        );
    }

    private MedicationSummaryResponse toSummaryResponseWithRelations(Medication medication) {
        Patient patient = patientRepository.findById(medication.getPatientId()).orElse(null);
        Product product = productRepository.findById(medication.getProductId()).orElse(null);

        return medicationMapper.toSummaryResponse(
                medication,
                patient != null ? patient.getFullName() : "Unknown",
                product != null ? product.getName() : medication.getMedicationName()
        );
    }}