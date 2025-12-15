package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.AdministrationRoute;
import com.pharmacy.assistant.domain.enums.MedicationFrequency;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private UUID productId;
    private String medicationName;

    // Dosage bilgileri
    private Double dosageAmount;
    private String dosageUnit;
    private String formattedDosage;
    private String dosageInstructions;

    // Schedule bilgileri
    private LocalDate startDate;
    private LocalDate endDate;
    private MedicationFrequency frequency;
    private List<LocalTime> timings;
    private Long remainingDays;
    private boolean isExpired;
    private boolean needsRefill;

    // Diğer bilgiler
    private AdministrationRoute administrationRoute;
    private MedicationStatus status;
    private String indication;
    private String sideEffects;
    private String contraindications;
    private String specialInstructions;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}