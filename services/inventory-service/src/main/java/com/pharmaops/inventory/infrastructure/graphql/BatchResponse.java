package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.domain.model.Batch;

import java.util.UUID;

public record BatchResponse(
        UUID id,
        UUID productId,
        String batchNumber,
        String expirationDate,
        int initialQuantity
) {
    public static BatchResponse from(Batch b) {
        return new BatchResponse(
                b.getId(), b.getProductId(), b.getBatchNumber(),
                b.getExpirationDate().toString(), b.getInitialQuantity()
        );
    }
}
