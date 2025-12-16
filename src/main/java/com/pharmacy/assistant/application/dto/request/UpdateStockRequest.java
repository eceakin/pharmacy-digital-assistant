package com.pharmacy.assistant.application.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {

    private String batchNumber;
    private LocalDate expiryDate;

    @Positive(message = "Miktar pozitif olmalıdır")
    private Integer quantity;

    @Positive(message = "Minimum stok seviyesi pozitif olmalıdır")
    private Integer minimumStockLevel;

    private String storageLocation;
    private String notes;
}
