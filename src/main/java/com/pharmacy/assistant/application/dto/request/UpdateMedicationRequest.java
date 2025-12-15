package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.AdministrationRoute;
import com.pharmacy.assistant.domain.enums.MedicationFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedicationRequest {

    // Dosage bilgileri
    @NotNull(message = "Doz miktarı girilmelidir")
    @Positive(message = "Doz miktarı pozitif olmalıdır")
    private Double dosageAmount;

    @NotBlank(message = "Doz birimi girilmelidir")
    private String dosageUnit;

    private String dosageInstructions;

    // Schedule bilgileri
    @NotNull(message = "Başlangıç tarihi girilmelidir")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Kullanım sıklığı seçilmelidir")
    private MedicationFrequency frequency;

    private List<LocalTime> timings;

    // Diğer bilgiler
    @NotNull(message = "Uygulama yolu seçilmelidir")
    private AdministrationRoute administrationRoute;

    private String indication;
    private String sideEffects;
    private String contraindications;
    private String specialInstructions;
    private String notes;
}