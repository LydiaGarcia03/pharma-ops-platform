package com.pharmaops.sales.application.usecase;

import com.pharmaops.sales.application.port.in.GetSaleUseCase;
import com.pharmaops.sales.application.port.out.SaleRepository;
import com.pharmaops.sales.domain.exception.SaleNotFoundException;
import com.pharmaops.sales.domain.model.Sale;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetSaleUseCaseImpl implements GetSaleUseCase {

    private final SaleRepository saleRepository;

    @Override
    public Sale getSale(UUID saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException(saleId));
    }
}
