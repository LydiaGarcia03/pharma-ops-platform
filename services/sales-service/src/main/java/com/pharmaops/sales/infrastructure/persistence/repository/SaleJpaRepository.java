package com.pharmaops.sales.infrastructure.persistence.repository;

import com.pharmaops.sales.infrastructure.persistence.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {
}
