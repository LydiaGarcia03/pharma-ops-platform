package com.pharmaops.sales.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "sale_id", nullable = false)
    private UUID saleId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "responsible_pharmacist_id")
    private UUID responsiblePharmacistId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
