package com.pharmaops.inventory.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @EmbeddedId
    private InventoryId id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "minimum_quantity", nullable = false)
    private int minimumQuantity;
}
