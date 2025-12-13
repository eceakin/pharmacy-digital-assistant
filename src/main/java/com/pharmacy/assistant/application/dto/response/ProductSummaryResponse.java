package com.pharmacy.assistant.application.dto.response;

import com.pharmacy.assistant.domain.enums.ProductCategory;
import com.pharmacy.assistant.domain.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponse {
    private UUID id;
    private String name;
    private String manufacturer;
    private ProductCategory category;
    private ProductStatus status;
    private String barcode;
    private BigDecimal sellingPrice;
    private BigDecimal priceWithVat;
}
