// PrescriptionResponse.java
package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private String prescriptionNumber;
    private PrescriptionType type;
    private PrescriptionStatus status;

    // Validity bilgileri
    private LocalDate issueDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer validityDays;
    private Long remainingDays;
    private boolean isValid;
    private boolean isExpired;
    private boolean isExpiringSoon;

    // Doktor bilgileri
    private String doctorName;
    private String doctorSpecialty;
    private String institution;

    // Diğer bilgiler
    private String diagnosis;
    private String notes;
    private Integer refillCount;
    private Integer refillsRemaining;
    private boolean canBeRefilled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// PrescriptionSummaryResponse.java


