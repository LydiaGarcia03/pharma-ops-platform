package com.pharmaops.sales.application.usecase;

import com.pharmaops.sales.application.port.in.ProcessReturnUseCase;
import com.pharmaops.sales.application.port.out.ReturnRepository;
import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.application.port.out.SaleRepository;
import com.pharmaops.sales.domain.exception.SaleNotFoundException;
import com.pharmaops.sales.domain.model.Return;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ProcessReturnUseCaseImpl implements ProcessReturnUseCase {

    private final SaleRepository saleRepository;
    private final ReturnRepository returnRepository;
    private final SaleEventPublisher eventPublisher;

    @Override
    public Return processReturn(Command command) {
        saleRepository.findById(command.saleId())
                .orElseThrow(() -> new SaleNotFoundException(command.saleId()));

        Return returnRecord = Return.builder()
                .id(UUID.randomUUID())
                .saleId(command.saleId())
                .productId(command.productId())
                .batchId(command.batchId())
                .userId(command.userId())
                .responsiblePharmacistId(command.responsiblePharmacistId())
                .quantity(command.quantity())
                .reason(command.reason())
                .correlationId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        Return saved = returnRepository.save(returnRecord);
        eventPublisher.publishReturnProcessed(saved);
        return saved;
    }
}
