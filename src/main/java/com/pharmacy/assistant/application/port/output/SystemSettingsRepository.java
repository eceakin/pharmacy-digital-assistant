package com.pharmacy.assistant.application.port.output;

import com.pharmacy.assistant.domain.model.settings.SystemSettings;

import java.util.Optional;

/**
 * Repository Port for System Settings
 */
public interface SystemSettingsRepository {

    SystemSettings save(SystemSettings settings);

    Optional<SystemSettings> findSettings();

    boolean exists();
}