package com.pharmaops.sales.infrastructure.persistence.adapter;

import com.pharmaops.sales.application.port.out.ReturnRepository;
import com.pharmaops.sales.domain.model.Return;
import com.pharmaops.sales.infrastructure.persistence.entity.ReturnEntity;
import com.pharmaops.sales.infrastructure.persistence.repository.ReturnJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReturnRepositoryAdapter implements ReturnRepository {

    private final ReturnJpaRepository returnJpaRepository;

    @Override
    public Return save(Return returnRecord) {
        return toDomain(returnJpaRepository.save(toEntity(returnRecord)));
    }

    private Return toDomain(ReturnEntity e) {
        return Return.builder()
                .id(e.getId())
                .saleId(e.getSaleId())
                .productId(e.getProductId())
                .batchId(e.getBatchId())
                .userId(e.getUserId())
                .responsiblePharmacistId(e.getResponsiblePharmacistId())
                .quantity(e.getQuantity())
                .reason(e.getReason())
                .correlationId(e.getCorrelationId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private ReturnEntity toEntity(Return r) {
        return ReturnEntity.builder()
                .id(r.getId())
                .saleId(r.getSaleId())
                .productId(r.getProductId())
                .batchId(r.getBatchId())
                .userId(r.getUserId())
                .responsiblePharmacistId(r.getResponsiblePharmacistId())
                .quantity(r.getQuantity())
                .reason(r.getReason())
                .correlationId(r.getCorrelationId())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
