// CreatePrescriptionRequest.java
package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.PrescriptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionRequest {

    @NotNull(message = "Hasta ID'si boş olamaz")
    private UUID patientId;

    @NotBlank(message = "Reçete numarası boş olamaz")
    private String prescriptionNumber;

    @NotNull(message = "Reçete türü seçilmelidir")
    private PrescriptionType type;

    // Validity bilgileri
    @NotNull(message = "Düzenlenme tarihi girilmelidir")
    private LocalDate issueDate;

    @NotNull(message = "Başlangıç tarihi girilmelidir")
    private LocalDate startDate;

    @NotNull(message = "Bitiş tarihi girilmelidir")
    private LocalDate endDate;

    @Positive(message = "Geçerlilik süresi pozitif olmalıdır")
    private Integer validityDays;

    // Doktor bilgileri
    @NotBlank(message = "Doktor adı boş olamaz")
    private String doctorName;

    private String doctorSpecialty;

    private String institution;

    // Diğer bilgiler
    private String diagnosis;

    private String notes;

    @PositiveOrZero(message = "Tekrar kullanım sayısı 0 veya daha büyük olmalıdır")    private Integer refillCount;
}



