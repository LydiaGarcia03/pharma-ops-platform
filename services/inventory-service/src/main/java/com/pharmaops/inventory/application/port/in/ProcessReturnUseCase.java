package com.pharmaops.inventory.application.port.in;

import java.util.UUID;

public interface ProcessReturnUseCase {

    void processReturn(Command command);

    record Command(UUID productId, UUID storeId, UUID batchId, int quantity, UUID correlationId) {}
}
