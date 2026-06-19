package com.pharmaops.identity.domain.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String identifier) {
        super("Store not found: " + identifier);
    }
}
