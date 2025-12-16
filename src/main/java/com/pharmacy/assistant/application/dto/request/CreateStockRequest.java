package com.pharmacy.assistant.application.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockRequest {

    @NotNull(message = "Ürün ID'si boş olamaz")
    private UUID productId;

    @NotNull(message = "Parti numarası boş olamaz")
    private String batchNumber;

    @NotNull(message = "Son kullanma tarihi girilmelidir")
    @FutureOrPresent(message = "Son kullanma tarihi bugünden önce olamaz")
    private LocalDate expiryDate;

    @NotNull(message = "Miktar girilmelidir")
    @Positive(message = "Miktar pozitif olmalıdır")
    private Integer quantity;

    @Positive(message = "Minimum stok seviyesi pozitif olmalıdır")
    private Integer minimumStockLevel;

    private String storageLocation;
    private String notes;
}

// ---

