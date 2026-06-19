package com.pharmaops.sales.application.usecase;

import com.pharmaops.sales.application.port.in.CreateSaleUseCase;
import com.pharmaops.sales.application.port.out.InventoryReservationPort;
import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.application.port.out.SaleRepository;
import com.pharmaops.sales.domain.exception.PharmacistRequiredException;
import com.pharmaops.sales.domain.exception.StockReservationException;
import com.pharmaops.sales.domain.model.Sale;
import com.pharmaops.sales.domain.model.SaleItem;
import com.pharmaops.sales.domain.model.SaleStatus;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateSaleUseCaseImpl implements CreateSaleUseCase {

    private final SaleRepository saleRepository;
    private final InventoryReservationPort inventoryReservationPort;
    private final SaleEventPublisher eventPublisher;

    @Override
    public Sale createSale(Command command) {
        boolean hasControlledProducts = command.items().stream()
                .anyMatch(ItemCommand::controlled);

        if ((hasControlledProducts || command.forced()) && command.responsiblePharmacistId() == null) {
            throw new PharmacistRequiredException();
        }

        UUID correlationId = UUID.randomUUID();
        List<SaleItem> saleItems = new ArrayList<>();

        for (ItemCommand item : command.items()) {
            InventoryReservationPort.Result reservation;
            try {
                reservation = inventoryReservationPort.reserve(
                        item.productId(), command.storeId(), item.quantity(), correlationId);
            } catch (StockReservationException e) {
                throw new StockReservationException(
                        "product " + item.productId() + ": " + e.getMessage());
            }

            saleItems.add(SaleItem.builder()
                    .id(UUID.randomUUID())
                    .productId(item.productId())
                    .batchId(reservation.batchId())
                    .quantity(item.quantity())
                    .unitPrice(item.unitPrice())
                    .build());
        }

        BigDecimal total = saleItems.stream()
                .map(SaleItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Sale sale = Sale.builder()
                .id(UUID.randomUUID())
                .storeId(command.storeId())
                .userId(command.userId())
                .responsiblePharmacistId(command.responsiblePharmacistId())
                .forced(command.forced())
                .status(SaleStatus.CONFIRMED)
                .items(saleItems)
                .total(total)
                .correlationId(correlationId)
                .createdAt(LocalDateTime.now())
                .build();

        Sale saved = saleRepository.save(sale);
        eventPublisher.publishSaleCompleted(saved);
        return saved;
    }
}
