package com.pharmaops.identity.infrastructure.persistence.repository;

import com.pharmaops.identity.infrastructure.persistence.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findByName(String name);
}
