package com.pharmaops.inventory.application.usecase;

import com.pharmaops.inventory.application.port.in.ReserveStockUseCase;
import com.pharmaops.inventory.application.port.out.BatchRepository;
import com.pharmaops.inventory.application.port.out.InventoryMovementRepository;
import com.pharmaops.inventory.application.port.out.InventoryRepository;
import com.pharmaops.inventory.application.port.out.InventoryEventPublisher;
import com.pharmaops.inventory.domain.exception.InsufficientStockException;
import com.pharmaops.inventory.domain.exception.ProductNotFoundException;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.InventoryItem;
import com.pharmaops.inventory.domain.model.InventoryMovement;
import com.pharmaops.inventory.domain.model.MovementType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReserveStockUseCaseImpl implements ReserveStockUseCase {

    private final InventoryRepository inventoryRepository;
    private final BatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryEventPublisher eventPublisher;

    @Override
    public Result reserveStock(Command command) {
        InventoryItem item = inventoryRepository
                .findByProductIdAndStoreId(command.productId(), command.storeId())
                .orElseThrow(() -> new ProductNotFoundException(
                        command.productId() + " at store " + command.storeId()));

        if (item.getQuantity() < command.quantity()) {
            throw new InsufficientStockException(
                    command.productId(), command.storeId(), command.quantity(), item.getQuantity());
        }

        // FEFO: lotes ordenados por expiration_date ASC — o que vence primeiro sai primeiro
        List<Batch> batches = batchRepository.findByProductIdOrderByExpirationDateAsc(command.productId());

        boolean deducted = false;
        Batch selectedBatch = null;
        for (Batch batch : batches) {
            boolean success = inventoryRepository.deductIfSufficient(
                    command.productId(), command.storeId(), command.quantity());
            if (success) {
                deducted = true;
                selectedBatch = batch;
                break;
            }
        }

        if (!deducted || selectedBatch == null) {
            throw new InsufficientStockException(
                    command.productId(), command.storeId(), command.quantity(), item.getQuantity());
        }

        int remainingQuantity = item.getQuantity() - command.quantity();

        movementRepository.save(InventoryMovement.builder()
                .id(UUID.randomUUID())
                .productId(command.productId())
                .storeId(command.storeId())
                .batchId(selectedBatch.getId())
                .type(MovementType.SALE_OUTFLOW)
                .quantity(command.quantity())
                .correlationId(command.correlationId())
                .createdAt(LocalDateTime.now())
                .build());

        InventoryItem updatedItem = item.withQuantity(remainingQuantity);
        eventPublisher.publishInventoryUpdated(updatedItem, selectedBatch, item.getQuantity(), MovementType.SALE_OUTFLOW);

        return new Result(selectedBatch, remainingQuantity);
    }
}
