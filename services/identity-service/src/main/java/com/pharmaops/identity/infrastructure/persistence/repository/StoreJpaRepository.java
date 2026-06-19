package com.pharmaops.identity.infrastructure.persistence.repository;

import com.pharmaops.identity.infrastructure.persistence.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, UUID> {
}
