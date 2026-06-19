package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.domain.model.Product;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String barcode,
        boolean controlled,
        String salePrice,
        boolean active
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getBarcode(),
                p.isControlled(), p.getSalePrice().toPlainString(), p.isActive()
        );
    }
}
