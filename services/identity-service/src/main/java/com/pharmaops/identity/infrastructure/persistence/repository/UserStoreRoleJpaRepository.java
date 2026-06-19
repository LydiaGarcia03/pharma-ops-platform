package com.pharmaops.identity.infrastructure.persistence.repository;

import com.pharmaops.identity.infrastructure.persistence.entity.UserStoreRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserStoreRoleJpaRepository extends JpaRepository<UserStoreRoleEntity, UUID> {
}
