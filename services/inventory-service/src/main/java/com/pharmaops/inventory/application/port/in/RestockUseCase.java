package com.pharmaops.inventory.application.port.in;

import java.util.UUID;

public interface RestockUseCase {

    void restock(Command command);

    record Command(UUID productId, UUID storeId, UUID batchId, int quantity, UUID correlationId) {}
}
