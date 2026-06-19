package com.pharmaops.inventory.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class InventoryItem {

    private final UUID productId;
    private final UUID storeId;
    private final int quantity;
    private final int minimumQuantity;

    public boolean isBelowMinimum() {
        return quantity <= minimumQuantity;
    }

    public boolean isDepleted() {
        return quantity == 0;
    }

    public InventoryItem withQuantity(int newQuantity) {
        return InventoryItem.builder()
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(newQuantity)
                .minimumQuantity(this.minimumQuantity)
                .build();
    }
}
