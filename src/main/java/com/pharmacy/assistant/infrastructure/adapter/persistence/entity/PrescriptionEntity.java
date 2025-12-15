package com.pharmacy.assistant.infrastructure.adapter.persistence.entity;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "prescription_number", nullable = false, unique = true, length = 50)
    private String prescriptionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PrescriptionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PrescriptionStatus status;

    // --- Validity Value Object Fields (Flattened) ---
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "validity_days")
    private Integer validityDays;

    // --- Doctor Info ---
    @Column(name = "doctor_name", nullable = false, length = 100)
    private String doctorName;

    @Column(name = "doctor_specialty", length = 100)
    private String doctorSpecialty;

    @Column(name = "institution", length = 150)
    private String institution;

    // --- Other Fields ---
    @Column(name = "diagnosis", length = 500)
    private String diagnosis;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "refill_count")
    private Integer refillCount;

    @Column(name = "refills_remaining")
    private Integer refillsRemaining;

    // --- Audit ---
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}