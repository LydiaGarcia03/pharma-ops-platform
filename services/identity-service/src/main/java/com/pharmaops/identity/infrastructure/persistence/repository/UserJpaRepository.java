package com.pharmaops.identity.infrastructure.persistence.repository;

import com.pharmaops.identity.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT COUNT(*) FROM user_store_roles usr
            JOIN profiles p ON usr.profile_id = p.id
            WHERE p.name = :profileName
            """, nativeQuery = true)
    long countByProfileName(@Param("profileName") String profileName);
}
