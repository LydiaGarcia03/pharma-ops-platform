package com.pharmaops.inventory.domain.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID productId, UUID storeId, int requested, int available) {
        super("Insufficient stock for product " + productId + " at store " + storeId
                + ": requested " + requested + ", available " + available);
    }
}
