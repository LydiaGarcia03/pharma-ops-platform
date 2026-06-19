package com.pharmaops.sales.domain.exception;

public class StockReservationException extends RuntimeException {
    public StockReservationException(String message) {
        super("Stock reservation failed: " + message);
    }
}
