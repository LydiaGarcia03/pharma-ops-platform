package com.pharmaops.inventory.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class InventoryMovement {

    private final UUID id;
    private final UUID productId;
    private final UUID storeId;
    private final UUID batchId;
    private final MovementType type;
    private final int quantity;
    private final UUID correlationId;
    private final LocalDateTime createdAt;
}
