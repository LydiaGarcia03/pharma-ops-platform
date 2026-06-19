package com.pharmaops.inventory.application.port.in;

import com.pharmaops.inventory.domain.model.Batch;

import java.time.LocalDate;
import java.util.UUID;

public interface AddBatchUseCase {

    Batch addBatch(Command command);

    record Command(UUID productId, String batchNumber, LocalDate expirationDate, int initialQuantity) {}
}
