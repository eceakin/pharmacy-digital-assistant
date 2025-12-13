package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPricing {
    private BigDecimal purchasePrice;  // Alış fiyatı
    private BigDecimal sellingPrice;   // Satış fiyatı
    private BigDecimal vatRate;        // KDV oranı (örn: 18.0)

    public BigDecimal getVatAmount() {
        if (sellingPrice == null || vatRate == null) {
            return BigDecimal.ZERO;
        }
        return sellingPrice.multiply(vatRate).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getPriceWithVat() {
        if (sellingPrice == null) {
            return BigDecimal.ZERO;
        }
        return sellingPrice.add(getVatAmount());
    }

    public BigDecimal getProfitMargin() {
        if (purchasePrice == null || sellingPrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return sellingPrice.subtract(purchasePrice)
                .divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}