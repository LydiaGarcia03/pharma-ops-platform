package com.pharmaops.sales.application.port.in;

import com.pharmaops.sales.domain.model.Sale;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreateSaleUseCase {

    Sale createSale(Command command);

    record Command(
            UUID storeId,
            UUID userId,
            UUID responsiblePharmacistId,
            boolean forced,
            List<ItemCommand> items
    ) {}

    record ItemCommand(
            UUID productId,
            boolean controlled,
            int quantity,
            BigDecimal unitPrice
    ) {}
}
