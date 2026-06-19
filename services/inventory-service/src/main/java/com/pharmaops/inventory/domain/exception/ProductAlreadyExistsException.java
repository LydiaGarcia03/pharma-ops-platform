package com.pharmaops.inventory.domain.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String barcode) {
        super("Product with barcode already exists: " + barcode);
    }
}
