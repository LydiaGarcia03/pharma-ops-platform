package com.pharmaops.sales.application.port.in;

import com.pharmaops.sales.domain.model.Sale;

import java.util.UUID;

public interface GetSaleUseCase {

    Sale getSale(UUID saleId);
}
