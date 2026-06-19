package com.pharmaops.inventory.application.usecase;

import com.pharmaops.inventory.application.port.in.AddBatchUseCase;
import com.pharmaops.inventory.application.port.out.BatchRepository;
import com.pharmaops.inventory.application.port.out.InventoryRepository;
import com.pharmaops.inventory.application.port.out.ProductRepository;
import com.pharmaops.inventory.domain.exception.ProductNotFoundException;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AddBatchUseCaseImpl implements AddBatchUseCase {

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Batch addBatch(Command command) {
        productRepository.findById(command.productId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        Batch batch = Batch.builder()
                .id(UUID.randomUUID())
                .productId(command.productId())
                .batchNumber(command.batchNumber())
                .expirationDate(command.expirationDate())
                .initialQuantity(command.initialQuantity())
                .build();
        batchRepository.save(batch);

        // AddBatch não tem store_id — o reabastecimento por loja é feito via RestockUseCase.
        // Aqui apenas registramos o lote no catálogo de lotes do produto.
        return batch;
    }
}
