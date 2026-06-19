package com.pharmaops.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

// @Embeddable marca esta classe como uma chave composta reutilizável pelo JPA.
// Deve implementar Serializable e ter equals/hashCode corretos para que o cache do JPA funcione.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InventoryId implements Serializable {

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "store_id")
    private UUID storeId;
}
