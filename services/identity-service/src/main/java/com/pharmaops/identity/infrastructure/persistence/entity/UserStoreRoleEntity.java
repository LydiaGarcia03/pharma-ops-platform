package com.pharmaops.identity.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_store_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreRoleEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    // nullable: null = ADMIN de rede (acesso global)
    @Column(name = "store_id")
    private UUID storeId;
}
