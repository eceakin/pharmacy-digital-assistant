package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionType;
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
public class PrescriptionSummaryResponse {
    private UUID id;
    private String patientName;
    private String prescriptionNumber;
    private PrescriptionType type;
    private PrescriptionStatus status;
    private LocalDate endDate;
    private Long remainingDays;
    private boolean isExpiringSoon;
    private Integer refillsRemaining;
}
