package com.pharmaops.inventory.domain.exception;

public class BatchNotFoundException extends RuntimeException {
    public BatchNotFoundException(Object identifier) {
        super("Batch not found: " + identifier);
    }
}
