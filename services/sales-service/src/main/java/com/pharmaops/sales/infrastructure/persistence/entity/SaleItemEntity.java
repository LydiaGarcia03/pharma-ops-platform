package com.pharmaops.sales.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "sale_id", nullable = false)
    private UUID saleId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
