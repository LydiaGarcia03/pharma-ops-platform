package com.pharmaops.sales.infrastructure.graphql;

import com.pharmaops.sales.domain.model.SaleItem;

import java.util.UUID;

public record SaleItemResponse(
        UUID id, UUID productId, UUID batchId, int quantity, String unitPrice
) {
    public static SaleItemResponse from(SaleItem item) {
        return new SaleItemResponse(
                item.getId(), item.getProductId(), item.getBatchId(),
                item.getQuantity(), item.getUnitPrice().toPlainString()
        );
    }
}
