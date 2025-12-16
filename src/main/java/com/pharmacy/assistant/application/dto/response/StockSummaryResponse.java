package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String batchNumber;
    private LocalDate expiryDate;
    private Integer quantity;
    private StockStatus status;
    private Long daysUntilExpiry;
    private Boolean isNearExpiry;
}
