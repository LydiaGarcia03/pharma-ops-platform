package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.domain.model.InventoryItem;

import java.util.UUID;

public record InventoryItemResponse(
        UUID productId,
        UUID storeId,
        int quantity,
        int minimumQuantity
) {
    public static InventoryItemResponse from(InventoryItem item) {
        return new InventoryItemResponse(
                item.getProductId(), item.getStoreId(),
                item.getQuantity(), item.getMinimumQuantity()
        );
    }
}
