package com.pharmaops.identity.infrastructure.persistence.adapter;

import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.domain.model.Store;
import com.pharmaops.identity.infrastructure.persistence.entity.StoreEntity;
import com.pharmaops.identity.infrastructure.persistence.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepository {

    private final StoreJpaRepository jpaRepository;

    @Override
    public Optional<Store> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Store save(Store store) {
        return toDomain(jpaRepository.save(toEntity(store)));
    }

    @Override
    public List<Store> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private Store toDomain(StoreEntity e) {
        return Store.builder()
                .id(e.getId())
                .name(e.getName())
                .taxId(e.getTaxId())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private StoreEntity toEntity(Store s) {
        return StoreEntity.builder()
                .id(s.getId())
                .name(s.getName())
                .taxId(s.getTaxId())
                .active(s.isActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
