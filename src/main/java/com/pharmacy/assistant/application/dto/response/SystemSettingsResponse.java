package com.pharmacy.assistant.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponse {
    private UUID id;

    // Bildirim Ayarları
    private Integer medicationExpiryWarningDays;
    private Integer prescriptionExpiryWarningDays;
    private Integer stockExpiryWarningDays;
    private LocalTime notificationTime;

    // Kanal Ayarları
    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;

    // Eczane Bilgileri
    private String pharmacyName;
    private String pharmacyPhone;
    private String pharmacyEmail;
    private String pharmacyAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}