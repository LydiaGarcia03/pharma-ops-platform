package com.pharmaops.inventory.infrastructure.persistence.adapter;

import com.pharmaops.inventory.application.port.out.InventoryMovementRepository;
import com.pharmaops.inventory.domain.model.InventoryMovement;
import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryMovementEntity;
import com.pharmaops.inventory.infrastructure.persistence.repository.InventoryMovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryMovementRepositoryAdapter implements InventoryMovementRepository {

    private final InventoryMovementJpaRepository movementJpaRepository;

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        return toDomain(movementJpaRepository.save(toEntity(movement)));
    }

    private InventoryMovement toDomain(InventoryMovementEntity e) {
        return InventoryMovement.builder()
                .id(e.getId())
                .productId(e.getProductId())
                .storeId(e.getStoreId())
                .batchId(e.getBatchId())
                .type(com.pharmaops.inventory.domain.model.MovementType.valueOf(e.getType()))
                .quantity(e.getQuantity())
                .correlationId(e.getCorrelationId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private InventoryMovementEntity toEntity(InventoryMovement m) {
        return InventoryMovementEntity.builder()
                .id(m.getId())
                .productId(m.getProductId())
                .storeId(m.getStoreId())
                .batchId(m.getBatchId())
                .type(m.getType().name())
                .quantity(m.getQuantity())
                .correlationId(m.getCorrelationId())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
