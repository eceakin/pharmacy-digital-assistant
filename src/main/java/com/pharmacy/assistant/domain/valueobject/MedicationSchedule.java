package com.pharmacy.assistant.domain.valueobject;

import com.pharmacy.assistant.domain.enums.MedicationFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationSchedule {
    private LocalDate startDate;
    private LocalDate endDate;
    private MedicationFrequency frequency;
    private List<LocalTime> timings = new ArrayList<>();  // Gün içi saatler

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return (startDate == null || !now.isBefore(startDate)) &&
                (endDate == null || !now.isAfter(endDate));
    }

    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(endDate);
    }

    public long getRemainingDays() {
        if (endDate == null) {
            return -1; // Süresiz
        }
        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(now, endDate);
    }
}
