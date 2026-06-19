package com.pharmaops.sales.infrastructure.graphql;

import com.pharmaops.sales.domain.model.Return;

import java.util.UUID;

public record ReturnResponse(
        UUID id, UUID saleId, UUID productId, UUID batchId,
        UUID userId, int quantity, String reason, String createdAt
) {
    public static ReturnResponse from(Return r) {
        return new ReturnResponse(
                r.getId(), r.getSaleId(), r.getProductId(), r.getBatchId(),
                r.getUserId(), r.getQuantity(), r.getReason(), r.getCreatedAt().toString()
        );
    }
}
