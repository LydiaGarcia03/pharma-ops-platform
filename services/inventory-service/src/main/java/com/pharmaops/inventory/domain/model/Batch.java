package com.pharmaops.inventory.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class Batch {

    private final UUID id;
    private final UUID productId;
    private final String batchNumber;
    private final LocalDate expirationDate;
    private final int initialQuantity;
}
