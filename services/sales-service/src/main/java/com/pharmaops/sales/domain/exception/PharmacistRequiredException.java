package com.pharmaops.sales.domain.exception;

public class PharmacistRequiredException extends RuntimeException {
    public PharmacistRequiredException() {
        super("A responsible pharmacist is required for controlled products or forced sales");
    }
}
