package com.pharmacy.assistant.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemSettingsRequest {

    @NotNull(message = "İlaç bitim uyarı günü girilmelidir")
    @Min(value = 1, message = "En az 1 gün olmalıdır")
    private Integer medicationExpiryWarningDays;

    @NotNull(message = "Reçete bitim uyarı günü girilmelidir")
    @Min(value = 1, message = "En az 1 gün olmalıdır")
    private Integer prescriptionExpiryWarningDays;

    @NotNull(message = "Stok SKT uyarı günü girilmelidir")
    @Min(value = 1, message = "En az 1 gün olmalıdır")
    private Integer stockExpiryWarningDays;

    @NotNull(message = "Bildirim saati girilmelidir")
    private LocalTime notificationTime;

    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;

    private String pharmacyName;
    private String pharmacyPhone;
    private String pharmacyEmail;
    private String pharmacyAddress;
}
