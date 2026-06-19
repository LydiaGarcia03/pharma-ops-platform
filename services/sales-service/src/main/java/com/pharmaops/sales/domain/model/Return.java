package com.pharmaops.sales.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Return {

    private final UUID id;
    private final UUID saleId;
    private final UUID productId;
    private final UUID batchId;
    private final UUID userId;
    private final UUID responsiblePharmacistId;
    private final int quantity;
    private final String reason;
    private final UUID correlationId;
    private final LocalDateTime createdAt;
}
