package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionValidity {
    private LocalDate issueDate;      // Reçete düzenlenme tarihi
    private LocalDate startDate;      // Geçerlilik başlangıcı
    private LocalDate endDate;        // Geçerlilik bitişi
    private Integer validityDays;     // Geçerlilik süresi (gün)

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (isExpired()) {
            return false;
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        return daysUntilExpiry <= daysThreshold && daysUntilExpiry >= 0;
    }

    public long getDaysUntilExpiry() {
        if (isExpired()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public long getRemainingDays() {
        return getDaysUntilExpiry();
    }
}