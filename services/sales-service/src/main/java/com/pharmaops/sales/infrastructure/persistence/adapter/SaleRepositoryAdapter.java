package com.pharmaops.sales.infrastructure.persistence.adapter;

import com.pharmaops.sales.application.port.out.SaleRepository;
import com.pharmaops.sales.domain.model.Sale;
import com.pharmaops.sales.domain.model.SaleItem;
import com.pharmaops.sales.domain.model.SaleStatus;
import com.pharmaops.sales.infrastructure.persistence.entity.SaleEntity;
import com.pharmaops.sales.infrastructure.persistence.entity.SaleItemEntity;
import com.pharmaops.sales.infrastructure.persistence.repository.SaleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SaleRepositoryAdapter implements SaleRepository {

    private final SaleJpaRepository saleJpaRepository;

    @Override
    public Sale save(Sale sale) {
        SaleEntity entity = toEntity(sale);
        return toDomain(saleJpaRepository.save(entity));
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return saleJpaRepository.findById(id).map(this::toDomain);
    }

    private Sale toDomain(SaleEntity e) {
        List<SaleItem> items = e.getItems().stream().map(this::itemToDomain).toList();
        return Sale.builder()
                .id(e.getId())
                .storeId(e.getStoreId())
                .userId(e.getUserId())
                .responsiblePharmacistId(e.getResponsiblePharmacistId())
                .forced(e.isForced())
                .status(SaleStatus.valueOf(e.getStatus()))
                .items(items)
                .total(e.getTotal())
                .correlationId(e.getCorrelationId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private SaleItem itemToDomain(SaleItemEntity e) {
        return SaleItem.builder()
                .id(e.getId())
                .saleId(e.getSaleId())
                .productId(e.getProductId())
                .batchId(e.getBatchId())
                .quantity(e.getQuantity())
                .unitPrice(e.getUnitPrice())
                .build();
    }

    private SaleEntity toEntity(Sale sale) {
        List<SaleItemEntity> itemEntities = sale.getItems().stream()
                .map(item -> SaleItemEntity.builder()
                        .id(item.getId())
                        .saleId(sale.getId())
                        .productId(item.getProductId())
                        .batchId(item.getBatchId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();

        return SaleEntity.builder()
                .id(sale.getId())
                .storeId(sale.getStoreId())
                .userId(sale.getUserId())
                .responsiblePharmacistId(sale.getResponsiblePharmacistId())
                .forced(sale.isForced())
                .status(sale.getStatus().name())
                .total(sale.getTotal())
                .correlationId(sale.getCorrelationId())
                .createdAt(sale.getCreatedAt())
                .items(new java.util.ArrayList<>(itemEntities))
                .build();
    }
}
