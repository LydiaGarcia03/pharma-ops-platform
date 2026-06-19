package com.pharmaops.inventory.infrastructure.persistence.repository;

import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementEntity, UUID> {
}
