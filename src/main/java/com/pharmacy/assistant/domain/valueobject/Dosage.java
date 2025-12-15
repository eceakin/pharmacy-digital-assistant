package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dosage {
    private Double amount;        // Doz miktarı (örn: 500)
    private String unit;          // Birim (mg, ml, tablet, vb.)
    private String instructions;  // Kullanım talimatları

    public String getFormattedDosage() {
        if (amount == null || unit == null) {
            return "Belirtilmemiş";
        }
        return amount + " " + unit;
    }

    public boolean isValid() {
        return amount != null && amount > 0 && unit != null && !unit.isEmpty();
    }
}
