package com.pharmaops.inventory.application.port.in;

import com.pharmaops.inventory.domain.model.Batch;

import java.util.UUID;

public interface ReserveStockUseCase {

    /**
     * Reserva (debita) estoque para uma venda.
     * Retorna o batch selecionado pelo critério FEFO (menor expiration_date).
     * Lança InsufficientStockException se quantity < requested.
     */
    Result reserveStock(Command command);

    record Command(UUID productId, UUID storeId, int quantity, UUID correlationId) {}
    record Result(Batch batch, int remainingQuantity) {}
}
