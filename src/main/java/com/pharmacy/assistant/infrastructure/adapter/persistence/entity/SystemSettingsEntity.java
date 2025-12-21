package com.pharmacy.assistant.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Bildirim Ayarları
    @Column(name = "medication_expiry_warning_days", nullable = false)
    private Integer medicationExpiryWarningDays;

    @Column(name = "prescription_expiry_warning_days", nullable = false)
    private Integer prescriptionExpiryWarningDays;

    @Column(name = "stock_expiry_warning_days", nullable = false)
    private Integer stockExpiryWarningDays;

    @Column(name = "notification_time", nullable = false)
    private LocalTime notificationTime;

    // Bildirim Kanalı Ayarları
    @Column(name = "email_notifications_enabled", nullable = false)
    private Boolean emailNotificationsEnabled;

    @Column(name = "sms_notifications_enabled", nullable = false)
    private Boolean smsNotificationsEnabled;

    // Eczane Bilgileri
    @Column(name = "pharmacy_name", length = 200)
    private String pharmacyName;

    @Column(name = "pharmacy_phone", length = 20)
    private String pharmacyPhone;

    @Column(name = "pharmacy_email", length = 100)
    private String pharmacyEmail;

    @Column(name = "pharmacy_address", length = 500)
    private String pharmacyAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}