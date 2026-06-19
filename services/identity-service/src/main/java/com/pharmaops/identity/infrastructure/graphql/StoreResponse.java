package com.pharmaops.identity.infrastructure.graphql;

import com.pharmaops.identity.domain.model.Store;

public record StoreResponse(
        String id,
        String name,
        String taxId,
        boolean active,
        String createdAt
) {
    static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId().toString(),
                store.getName(),
                store.getTaxId(),
                store.isActive(),
                store.getCreatedAt().toString()
        );
    }
}
