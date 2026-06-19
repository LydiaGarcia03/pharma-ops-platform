package com.pharmaops.inventory.infrastructure.persistence.adapter;

import com.pharmaops.inventory.application.port.out.BatchRepository;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.infrastructure.persistence.entity.BatchEntity;
import com.pharmaops.inventory.infrastructure.persistence.repository.BatchJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BatchRepositoryAdapter implements BatchRepository {

    private final BatchJpaRepository batchJpaRepository;

    @Override
    public Batch save(Batch batch) {
        return toDomain(batchJpaRepository.save(toEntity(batch)));
    }

    @Override
    public Optional<Batch> findById(UUID id) {
        return batchJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Batch> findByProductIdOrderByExpirationDateAsc(UUID productId) {
        return batchJpaRepository.findByProductIdOrderByExpirationDateAsc(productId)
                .stream().map(this::toDomain).toList();
    }

    private Batch toDomain(BatchEntity e) {
        return Batch.builder()
                .id(e.getId())
                .productId(e.getProductId())
                .batchNumber(e.getBatchNumber())
                .expirationDate(e.getExpirationDate())
                .initialQuantity(e.getInitialQuantity())
                .build();
    }

    private BatchEntity toEntity(Batch b) {
        return BatchEntity.builder()
                .id(b.getId())
                .productId(b.getProductId())
                .batchNumber(b.getBatchNumber())
                .expirationDate(b.getExpirationDate())
                .initialQuantity(b.getInitialQuantity())
                .build();
    }
}
