package com.pharmacy.assistant.domain.model.prescription;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.domain.enums.PrescriptionType;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import com.pharmacy.assistant.domain.valueobject.PrescriptionValidity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Prescription extends BaseEntity {
    private UUID patientId;                          // Hasta ID'si
    private String prescriptionNumber;               // Reçete numarası (unique)
    private PrescriptionType type;                   // Reçete türü
    private PrescriptionStatus status;               // Reçete durumu
    private PrescriptionValidity validity;           // Geçerlilik bilgileri
    private String doctorName;                       // Doktor adı
    private String doctorSpecialty;                  // Doktor uzmanlık alanı
    private String institution;                      // Kurum/Hastane
    private String diagnosis;                        // Tanı/Hastalık
    private String notes;                            // Notlar
    private Integer refillCount;                     // Tekrar kullanım sayısı
    private Integer refillsRemaining;                // Kalan tekrar kullanım

    public boolean isActive() {
        return PrescriptionStatus.ACTIVE.equals(status) &&
                validity != null &&
                validity.isValid();
    }

    public boolean isExpired() {
        return validity != null && validity.isExpired();
    }

    public boolean needsRenewal(int daysThreshold) {
        if (validity == null) {
            return false;
        }
        return validity.isExpiringSoon(daysThreshold);
    }

    public boolean canBeRefilled() {
        return refillsRemaining != null &&
                refillsRemaining > 0 &&
                isActive();
    }

    public void useRefill() {
        if (canBeRefilled()) {
            this.refillsRemaining--;
            if (this.refillsRemaining == 0) {
                this.status = PrescriptionStatus.USED;
            }
        }
    }

    public void activate() {
        this.status = PrescriptionStatus.ACTIVE;
    }

    public void expire() {
        this.status = PrescriptionStatus.EXPIRED;
    }

    public void cancel() {
        this.status = PrescriptionStatus.CANCELLED;
    }

    public void markAsUsed() {
        this.status = PrescriptionStatus.USED;
    }

    public long getRemainingDays() {
        if (validity == null) {
            return -1;
        }
        return validity.getRemainingDays();
    }
}