package com.pharmaops.sales.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Sale {

    private final UUID id;
    private final UUID storeId;
    private final UUID userId;
    private final UUID responsiblePharmacistId;
    private final boolean forced;
    private final SaleStatus status;
    @Builder.Default
    private final List<SaleItem> items = List.of();
    private final BigDecimal total;
    private final UUID correlationId;
    private final LocalDateTime createdAt;

    public boolean requiresPharmacist(boolean hasControlledProducts) {
        return forced || hasControlledProducts;
    }
}
