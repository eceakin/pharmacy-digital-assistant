package com.pharmacy.assistant.application.service.settings;

import com.pharmacy.assistant.application.dto.request.UpdateSystemSettingsRequest;
import com.pharmacy.assistant.application.dto.response.SystemSettingsResponse;
import com.pharmacy.assistant.application.port.output.SystemSettingsRepository;
import com.pharmacy.assistant.domain.model.settings.SystemSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * System Settings Service
 * Manages application-wide configuration settings
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SystemSettingsService {

    private final SystemSettingsRepository settingsRepository;

    /**
     * Get current system settings
     * If no settings exist, creates default settings
     */
    @Transactional(readOnly = true)
    public SystemSettingsResponse getSettings() {
        log.info("Fetching system settings");

        SystemSettings settings = settingsRepository.findSettings()
                .orElseGet(this::createDefaultSettings);

        return toResponse(settings);
    }

    /**
     * Update system settings
     */
    public SystemSettingsResponse updateSettings(UpdateSystemSettingsRequest request) {
        log.info("Updating system settings");

        SystemSettings settings = settingsRepository.findSettings()
                .orElseGet(SystemSettings::new);

        // Update fields
        settings.setMedicationExpiryWarningDays(request.getMedicationExpiryWarningDays());
        settings.setPrescriptionExpiryWarningDays(request.getPrescriptionExpiryWarningDays());
        settings.setStockExpiryWarningDays(request.getStockExpiryWarningDays());
        settings.setNotificationTime(request.getNotificationTime());

        if (request.getEmailNotificationsEnabled() != null) {
            settings.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getSmsNotificationsEnabled() != null) {
            settings.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        }

        // Pharmacy info
        settings.setPharmacyName(request.getPharmacyName());
        settings.setPharmacyPhone(request.getPharmacyPhone());
        settings.setPharmacyEmail(request.getPharmacyEmail());
        settings.setPharmacyAddress(request.getPharmacyAddress());

        settings.prePersist();

        SystemSettings savedSettings = settingsRepository.save(settings);

        log.info("System settings updated successfully");
        return toResponse(savedSettings);
    }

    /**
     * Reset settings to defaults
     */
    public SystemSettingsResponse resetToDefaults() {
        log.info("Resetting system settings to defaults");

        SystemSettings defaultSettings = new SystemSettings();
        defaultSettings.prePersist();

        SystemSettings savedSettings = settingsRepository.save(defaultSettings);

        log.info("System settings reset to defaults");
        return toResponse(savedSettings);
    }

    /**
     * Get medication expiry warning days
     */
    @Transactional(readOnly = true)
    public Integer getMedicationExpiryWarningDays() {
        return settingsRepository.findSettings()
                .map(SystemSettings::getMedicationExpiryWarningDays)
                .orElse(3); // Default 3 days
    }

    /**
     * Get prescription expiry warning days
     */
    @Transactional(readOnly = true)
    public Integer getPrescriptionExpiryWarningDays() {
        return settingsRepository.findSettings()
                .map(SystemSettings::getPrescriptionExpiryWarningDays)
                .orElse(7); // Default 7 days
    }

    /**
     * Get stock expiry warning days
     */
    @Transactional(readOnly = true)
    public Integer getStockExpiryWarningDays() {
        return settingsRepository.findSettings()
                .map(SystemSettings::getStockExpiryWarningDays)
                .orElse(90); // Default 90 days
    }

    /**
     * Check if email notifications are enabled
     */
    @Transactional(readOnly = true)
    public boolean isEmailNotificationsEnabled() {
        return settingsRepository.findSettings()
                .map(SystemSettings::getEmailNotificationsEnabled)
                .orElse(true);
    }

    // --- PRIVATE METHODS ---

    private SystemSettings createDefaultSettings() {
        log.info("Creating default system settings");
        SystemSettings defaultSettings = new SystemSettings();
        defaultSettings.prePersist();
        return settingsRepository.save(defaultSettings);
    }

    private SystemSettingsResponse toResponse(SystemSettings settings) {
        return SystemSettingsResponse.builder()
                .id(settings.getId())
                .medicationExpiryWarningDays(settings.getMedicationExpiryWarningDays())
                .prescriptionExpiryWarningDays(settings.getPrescriptionExpiryWarningDays())
                .stockExpiryWarningDays(settings.getStockExpiryWarningDays())
                .notificationTime(settings.getNotificationTime())
                .emailNotificationsEnabled(settings.getEmailNotificationsEnabled())
                .smsNotificationsEnabled(settings.getSmsNotificationsEnabled())
                .pharmacyName(settings.getPharmacyName())
                .pharmacyPhone(settings.getPharmacyPhone())
                .pharmacyEmail(settings.getPharmacyEmail())
                .pharmacyAddress(settings.getPharmacyAddress())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}