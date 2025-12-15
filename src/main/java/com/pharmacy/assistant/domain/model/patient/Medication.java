package com.pharmacy.assistant.domain.model.patient;

import com.pharmacy.assistant.domain.enums.AdministrationRoute;
import com.pharmacy.assistant.domain.enums.MedicationStatus;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import com.pharmacy.assistant.domain.valueobject.Dosage;
import com.pharmacy.assistant.domain.valueobject.MedicationSchedule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Medication extends BaseEntity {
    private UUID patientId;       // Hasta ID'si
    private UUID productId;       // Ürün ID'si (Product entity'den)
    private String medicationName; // İlaç adı (denormalize - performans için)
    private Dosage dosage;
    private MedicationSchedule schedule;
    private AdministrationRoute administrationRoute;
    private MedicationStatus status;
    private String indication;     // Kullanım amacı/endikasyon
    private String sideEffects;    // Yan etkiler
    private String contraindications; // Kontrendikasyonlar
    private String specialInstructions; // Özel talimatlar
    private String notes;

    public boolean isActive() {
        return MedicationStatus.ACTIVE.equals(status) &&
                schedule != null &&
                schedule.isActive();
    }

    public boolean needsRefill() {
        if (schedule == null || schedule.getEndDate() == null) {
            return false;
        }
        long remainingDays = schedule.getRemainingDays();
        return remainingDays > 0 && remainingDays <= 7; // 7 gün veya daha az kaldıysa
    }

    public boolean isExpired() {
        return schedule != null && schedule.isExpired();
    }

    public void activate() {
        this.status = MedicationStatus.ACTIVE;
    }

    public void discontinue() {
        this.status = MedicationStatus.DISCONTINUED;
    }

    public void putOnHold() {
        this.status = MedicationStatus.ON_HOLD;
    }

    public void complete() {
        this.status = MedicationStatus.COMPLETED;
    }
}