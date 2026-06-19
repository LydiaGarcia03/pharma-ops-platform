package com.pharmaops.inventory.application.usecase;

import com.pharmaops.inventory.application.port.in.RestockUseCase;
import com.pharmaops.inventory.application.port.out.BatchRepository;
import com.pharmaops.inventory.application.port.out.InventoryMovementRepository;
import com.pharmaops.inventory.application.port.out.InventoryRepository;
import com.pharmaops.inventory.domain.exception.BatchNotFoundException;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.InventoryItem;
import com.pharmaops.inventory.domain.model.InventoryMovement;
import com.pharmaops.inventory.domain.model.MovementType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class RestockUseCaseImpl implements RestockUseCase {

    private final BatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    @Override
    public void restock(Command command) {
        batchRepository.findById(command.batchId())
                .orElseThrow(() -> new BatchNotFoundException(command.batchId()));

        InventoryItem current = inventoryRepository
                .findByProductIdAndStoreId(command.productId(), command.storeId())
                .orElse(InventoryItem.builder()
                        .productId(command.productId())
                        .storeId(command.storeId())
                        .quantity(0)
                        .minimumQuantity(0)
                        .build());

        inventoryRepository.save(current.withQuantity(current.getQuantity() + command.quantity()));

        movementRepository.save(InventoryMovement.builder()
                .id(UUID.randomUUID())
                .productId(command.productId())
                .storeId(command.storeId())
                .batchId(command.batchId())
                .type(MovementType.RESTOCK_INFLOW)
                .quantity(command.quantity())
                .correlationId(command.correlationId())
                .createdAt(LocalDateTime.now())
                .build());
    }
}
