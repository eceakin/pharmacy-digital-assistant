package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.MedicationFrequency;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationSummaryResponse {
    private UUID id;
    private String patientName;
    private String medicationName;
    private String formattedDosage;
    private MedicationFrequency frequency;
    private MedicationStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long remainingDays;
    private boolean needsRefill;
}
