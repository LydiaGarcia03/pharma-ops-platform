package com.pharmaops.inventory.infrastructure.persistence.repository;

import com.pharmaops.inventory.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);
}
