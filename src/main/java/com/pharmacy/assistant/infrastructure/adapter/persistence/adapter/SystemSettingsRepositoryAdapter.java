package com.pharmacy.assistant.infrastructure.adapter.persistence.adapter;

import com.pharmacy.assistant.application.port.output.SystemSettingsRepository;
import com.pharmacy.assistant.domain.model.settings.SystemSettings;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.SystemSettingsEntity;
import com.pharmacy.assistant.infrastructure.adapter.persistence.jpa.SystemSettingsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SystemSettingsRepositoryAdapter implements SystemSettingsRepository {

    private final SystemSettingsJpaRepository jpaRepository;

    @Override
    public SystemSettings save(SystemSettings settings) {
        SystemSettingsEntity entity = toEntity(settings);
        SystemSettingsEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<SystemSettings> findSettings() {
        return jpaRepository.findFirstByOrderByCreatedAtAsc()
                .map(this::toDomain);
    }

    @Override
    public boolean exists() {
        return jpaRepository.count() > 0;
    }

    // --- MAPPING METHODS ---

    private SystemSettingsEntity toEntity(SystemSettings domain) {
        SystemSettingsEntity entity = new SystemSettingsEntity();
        entity.setId(domain.getId());
        entity.setMedicationExpiryWarningDays(domain.getMedicationExpiryWarningDays());
        entity.setPrescriptionExpiryWarningDays(domain.getPrescriptionExpiryWarningDays());
        entity.setStockExpiryWarningDays(domain.getStockExpiryWarningDays());
        entity.setNotificationTime(domain.getNotificationTime());
        entity.setEmailNotificationsEnabled(domain.getEmailNotificationsEnabled());
        entity.setSmsNotificationsEnabled(domain.getSmsNotificationsEnabled());
        entity.setPharmacyName(domain.getPharmacyName());
        entity.setPharmacyPhone(domain.getPharmacyPhone());
        entity.setPharmacyEmail(domain.getPharmacyEmail());
        entity.setPharmacyAddress(domain.getPharmacyAddress());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private SystemSettings toDomain(SystemSettingsEntity entity) {
        SystemSettings domain = new SystemSettings();
        domain.setId(entity.getId());
        domain.setMedicationExpiryWarningDays(entity.getMedicationExpiryWarningDays());
        domain.setPrescriptionExpiryWarningDays(entity.getPrescriptionExpiryWarningDays());
        domain.setStockExpiryWarningDays(entity.getStockExpiryWarningDays());
        domain.setNotificationTime(entity.getNotificationTime());
        domain.setEmailNotificationsEnabled(entity.getEmailNotificationsEnabled());
        domain.setSmsNotificationsEnabled(entity.getSmsNotificationsEnabled());
        domain.setPharmacyName(entity.getPharmacyName());
        domain.setPharmacyPhone(entity.getPharmacyPhone());
        domain.setPharmacyEmail(entity.getPharmacyEmail());
        domain.setPharmacyAddress(entity.getPharmacyAddress());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }
}