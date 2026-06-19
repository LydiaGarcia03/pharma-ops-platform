package com.pharmaops.sales.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class SaleItem {

    private final UUID id;
    private final UUID saleId;
    private final UUID productId;
    private final UUID batchId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
