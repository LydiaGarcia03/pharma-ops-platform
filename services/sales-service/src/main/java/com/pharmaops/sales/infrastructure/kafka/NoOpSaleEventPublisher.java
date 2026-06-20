package com.pharmaops.sales.infrastructure.kafka;

import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.domain.model.Return;
import com.pharmaops.sales.domain.model.Sale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Mantida apenas para referência. Substituída pelo KafkaSaleEventPublisher (Fase 3).
@Slf4j
public class NoOpSaleEventPublisher implements SaleEventPublisher {

    @Override
    public void publishSaleCompleted(Sale sale) {
        log.info("[NO-OP] SaleCompleted event skipped — Kafka not yet configured. saleId={} total={}",
                sale.getId(), sale.getTotal());
    }

    @Override
    public void publishReturnProcessed(Return returnRecord, java.util.UUID storeId) {
        log.info("[NO-OP] ReturnProcessed event skipped — Kafka not yet configured. returnId={} saleId={}",
                returnRecord.getId(), returnRecord.getSaleId());
    }
}
