package com.pharmaops.inventory.infrastructure.persistence.repository;

import com.pharmaops.inventory.infrastructure.persistence.entity.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BatchJpaRepository extends JpaRepository<BatchEntity, UUID> {

    List<BatchEntity> findByProductIdOrderByExpirationDateAsc(UUID productId);
}
