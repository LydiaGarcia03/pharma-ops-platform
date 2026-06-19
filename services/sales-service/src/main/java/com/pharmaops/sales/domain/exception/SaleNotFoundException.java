package com.pharmaops.sales.domain.exception;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(Object identifier) {
        super("Sale not found: " + identifier);
    }
}
