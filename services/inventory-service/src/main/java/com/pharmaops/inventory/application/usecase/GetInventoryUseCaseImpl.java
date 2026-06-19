package com.pharmaops.inventory.application.usecase;

import com.pharmaops.inventory.application.port.in.GetInventoryUseCase;
import com.pharmaops.inventory.application.port.out.InventoryRepository;
import com.pharmaops.inventory.domain.exception.ProductNotFoundException;
import com.pharmaops.inventory.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetInventoryUseCaseImpl implements GetInventoryUseCase {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryItem getInventory(UUID productId, UUID storeId) {
        return inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ProductNotFoundException(productId + " at store " + storeId));
    }
}
