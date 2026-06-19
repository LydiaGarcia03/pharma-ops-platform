package com.pharmaops.inventory.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Object identifier) {
        super("Product not found: " + identifier);
    }
}
