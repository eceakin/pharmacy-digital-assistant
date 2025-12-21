package com.pharmacy.assistant.infrastructure.adapter.web.settings;

import com.pharmacy.assistant.application.dto.request.UpdateSystemSettingsRequest;
import com.pharmacy.assistant.application.dto.response.SystemSettingsResponse;
import com.pharmacy.assistant.application.service.settings.SystemSettingsService;
import com.pharmacy.assistant.infrastructure.adapter.web.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService settingsService;

    /**
     * GET /api/settings
     * Get current system settings
     */
    @GetMapping
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> getSettings() {
        log.info("REST: Fetching system settings");

        SystemSettingsResponse response = settingsService.getSettings();

        return ResponseEntity.ok(
                ApiResponse.success("Sistem ayarları getirildi", response)
        );
    }

    /**
     * PUT /api/settings
     * Update system settings
     */
    @PutMapping
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> updateSettings(
            @Valid @RequestBody UpdateSystemSettingsRequest request) {

        log.info("REST: Updating system settings");

        SystemSettingsResponse response = settingsService.updateSettings(request);

        return ResponseEntity.ok(
                ApiResponse.success("Sistem ayarları başarıyla güncellendi", response)
        );
    }

    /**
     * POST /api/settings/reset
     * Reset settings to defaults
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> resetSettings() {
        log.info("REST: Resetting system settings to defaults");

        SystemSettingsResponse response = settingsService.resetToDefaults();

        return ResponseEntity.ok(
                ApiResponse.success("Sistem ayarları varsayılana sıfırlandı", response)
        );
    }
}