package com.pharmaops.inventory.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaleCompletedConsumer {

    // O débito de estoque, registro de movimento SALE_OUTFLOW e publicação de InventoryUpdated
    // já acontecem no fluxo HTTP síncrono (ReserveStockUseCaseImpl).
    // Este consumer existe para auditoria e futura reconciliação (ex: relatórios — Fase 5).
    @KafkaListener(topics = "${pharmaops.kafka.topics.sales-completed}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received SaleCompleted event: partition={} offset={} storeId(key)={}",
                record.partition(), record.offset(), record.key());
    }
}
