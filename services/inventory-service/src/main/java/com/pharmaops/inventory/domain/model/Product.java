package com.pharmaops.inventory.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class Product {

    private final UUID id;
    private final String name;
    private final String barcode;
    private final boolean controlled;
    private final BigDecimal salePrice;
    private final boolean active;
}
