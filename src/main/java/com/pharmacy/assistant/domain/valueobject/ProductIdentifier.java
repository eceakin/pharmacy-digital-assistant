package com.pharmacy.assistant.domain.valueobject;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdentifier {
    private String barcode;
    private String qrCode;
    private String serialNumber;

    public boolean hasBarcode() {
        return barcode != null && !barcode.isEmpty();
    }

    public boolean hasQrCode() {
        return qrCode != null && !qrCode.isEmpty();
    }

    public boolean hasSerialNumber() {
        return serialNumber != null && !serialNumber.isEmpty();
    }
}