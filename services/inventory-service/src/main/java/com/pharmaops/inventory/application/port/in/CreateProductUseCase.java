package com.pharmaops.inventory.application.port.in;

import com.pharmaops.inventory.domain.model.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {

    Product createProduct(Command command);

    record Command(String name, String barcode, boolean controlled, BigDecimal salePrice) {}
}
