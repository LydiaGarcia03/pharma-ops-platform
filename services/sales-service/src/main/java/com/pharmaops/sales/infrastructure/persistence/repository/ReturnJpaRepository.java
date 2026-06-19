package com.pharmaops.sales.infrastructure.persistence.repository;

import com.pharmaops.sales.infrastructure.persistence.entity.ReturnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReturnJpaRepository extends JpaRepository<ReturnEntity, UUID> {
}
