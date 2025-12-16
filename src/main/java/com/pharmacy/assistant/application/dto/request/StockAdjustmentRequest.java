package com.pharmacy.assistant.application.dto.request;

import com.pharmacy.assistant.domain.enums.StockTransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {

    @NotNull(message = "Stok ID'si boş olamaz")
    private UUID stockId;

    @NotNull(message = "İşlem tipi seçilmelidir")
    private StockTransactionType transactionType;

    @NotNull(message = "Miktar girilmelidir")
    @Positive(message = "Miktar pozitif olmalıdır")
    private Integer quantity;

    private String reason;
    private String notes;
}
