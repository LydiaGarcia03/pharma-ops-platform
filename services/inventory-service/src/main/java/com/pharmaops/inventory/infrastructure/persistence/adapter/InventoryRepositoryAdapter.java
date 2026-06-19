package com.pharmaops.inventory.infrastructure.persistence.adapter;

import com.pharmaops.inventory.application.port.out.InventoryRepository;
import com.pharmaops.inventory.domain.model.InventoryItem;
import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryEntity;
import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryId;
import com.pharmaops.inventory.infrastructure.persistence.repository.InventoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public InventoryItem save(InventoryItem item) {
        return toDomain(inventoryJpaRepository.save(toEntity(item)));
    }

    @Override
    public Optional<InventoryItem> findByProductIdAndStoreId(UUID productId, UUID storeId) {
        return inventoryJpaRepository.findById_ProductIdAndId_StoreId(productId, storeId)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public boolean deductIfSufficient(UUID productId, UUID storeId, int quantity) {
        return inventoryJpaRepository.deductIfSufficient(productId, storeId, quantity) > 0;
    }

    private InventoryItem toDomain(InventoryEntity e) {
        return InventoryItem.builder()
                .productId(e.getId().getProductId())
                .storeId(e.getId().getStoreId())
                .quantity(e.getQuantity())
                .minimumQuantity(e.getMinimumQuantity())
                .build();
    }

    private InventoryEntity toEntity(InventoryItem item) {
        return InventoryEntity.builder()
                .id(new InventoryId(item.getProductId(), item.getStoreId()))
                .quantity(item.getQuantity())
                .minimumQuantity(item.getMinimumQuantity())
                .build();
    }
}
