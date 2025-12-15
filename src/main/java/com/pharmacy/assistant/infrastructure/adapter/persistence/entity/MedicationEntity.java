package com.pharmacy.assistant.infrastructure.adapter.persistence.entity;

import com.pharmacy.assistant.domain.enums.AdministrationRoute;
import com.pharmacy.assistant.domain.enums.MedicationFrequency;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    // --- Dosage Value Object (Flattened/Düzleştirilmiş) ---
    // Dosage.amount (Double)
    @Column(name = "dosage_amount")
    private Double dosageAmount;

    // Dosage.unit (String)
    @Column(name = "dosage_unit", length = 50)
    private String dosageUnit;

    // Dosage.instructions (String)
    @Column(name = "dosage_instructions", length = 500)
    private String dosageInstructions;

    // --- MedicationSchedule Value Object (Flattened) ---
    @Column(name = "schedule_start_date")
    private LocalDate scheduleStartDate;

    @Column(name = "schedule_end_date")
    private LocalDate scheduleEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_frequency", length = 50)
    private MedicationFrequency scheduleFrequency;

    // List<LocalTime> için Yan Tablo Yapılandırması
    // Bu, "medication_timings" adında, medication_id ve timing sütunları olan
    // ayrı bir tablo oluşturur.
    @ElementCollection(fetch = FetchType.EAGER) // Saatler genellikle ilaçla birlikte hemen lazım olur
    @CollectionTable(
            name = "medication_timings",
            joinColumns = @JoinColumn(name = "medication_id")
    )
    @Column(name = "timing")
    private List<LocalTime> scheduleTimings = new ArrayList<>();

    // --- Diğer Alanlar ---
    @Enumerated(EnumType.STRING)
    @Column(name = "administration_route", length = 50)
    private AdministrationRoute administrationRoute;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MedicationStatus status;

    @Column(name = "indication", length = 500)
    private String indication;

    @Column(name = "side_effects", length = 1000)
    private String sideEffects;

    @Column(name = "contraindications", length = 1000)
    private String contraindications;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "notes", length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}