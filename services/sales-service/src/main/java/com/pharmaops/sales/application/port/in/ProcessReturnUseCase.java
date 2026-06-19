package com.pharmaops.sales.application.port.in;

import com.pharmaops.sales.domain.model.Return;

import java.util.UUID;

public interface ProcessReturnUseCase {

    Return processReturn(Command command);

    record Command(
            UUID saleId,
            UUID productId,
            UUID batchId,
            UUID userId,
            UUID responsiblePharmacistId,
            int quantity,
            String reason
    ) {}
}
