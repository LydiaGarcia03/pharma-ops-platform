package com.pharmaops.sales.infrastructure.graphql;

import com.pharmaops.sales.domain.model.Sale;
import com.pharmaops.sales.domain.model.SaleItem;

import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id, UUID storeId, UUID userId, UUID responsiblePharmacistId,
        boolean forced, String status, List<SaleItemResponse> items,
        String total, UUID correlationId, String createdAt
) {
    public static SaleResponse from(Sale s) {
        return new SaleResponse(
                s.getId(), s.getStoreId(), s.getUserId(), s.getResponsiblePharmacistId(),
                s.isForced(), s.getStatus().name(),
                s.getItems().stream().map(SaleItemResponse::from).toList(),
                s.getTotal().toPlainString(), s.getCorrelationId(), s.getCreatedAt().toString()
        );
    }
}
