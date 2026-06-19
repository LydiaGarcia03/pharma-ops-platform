package com.pharmaops.inventory.application.port.in;

import com.pharmaops.inventory.domain.model.InventoryItem;

import java.util.UUID;

public interface GetInventoryUseCase {

    InventoryItem getInventory(UUID productId, UUID storeId);
}
