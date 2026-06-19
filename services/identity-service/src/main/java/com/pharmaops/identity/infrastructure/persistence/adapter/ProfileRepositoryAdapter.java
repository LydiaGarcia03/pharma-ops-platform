package com.pharmaops.identity.infrastructure.persistence.adapter;

import com.pharmaops.identity.application.port.out.ProfileRepository;
import com.pharmaops.identity.domain.model.Permission;
import com.pharmaops.identity.domain.model.Profile;
import com.pharmaops.identity.infrastructure.persistence.entity.PermissionEntity;
import com.pharmaops.identity.infrastructure.persistence.entity.ProfileEntity;
import com.pharmaops.identity.infrastructure.persistence.entity.UserStoreRoleEntity;
import com.pharmaops.identity.infrastructure.persistence.repository.ProfileJpaRepository;
import com.pharmaops.identity.infrastructure.persistence.repository.UserStoreRoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final ProfileJpaRepository profileJpaRepository;
    private final UserStoreRoleJpaRepository userStoreRoleJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(UUID id) {
        return profileJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByName(String name) {
        return profileJpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public Profile save(Profile profile) {
        return toDomain(profileJpaRepository.save(toEntity(profile)));
    }

    @Override
    public void assignToUser(UUID userId, UUID profileId, UUID storeId) {
        UserStoreRoleEntity role = UserStoreRoleEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .profileId(profileId)
                .storeId(storeId)
                .build();
        userStoreRoleJpaRepository.save(role);
    }

    private Profile toDomain(ProfileEntity e) {
        Set<Permission> permissions = e.getPermissions().stream()
                .map(this::permissionToDomain)
                .collect(Collectors.toSet());

        return Profile.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .permissions(permissions)
                .build();
    }

    private ProfileEntity toEntity(Profile p) {
        return ProfileEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    private Permission permissionToDomain(PermissionEntity e) {
        return Permission.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .build();
    }
}
