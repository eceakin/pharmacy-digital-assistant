package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.PrescriptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrescriptionRequest {

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

    @Positive(message = "Tekrar kullanım sayısı pozitif olmalıdır")
    private Integer refillCount;
}
