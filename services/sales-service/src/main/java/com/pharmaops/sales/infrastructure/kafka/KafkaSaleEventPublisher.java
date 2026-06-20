package com.pharmaops.sales.infrastructure.kafka;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.domain.model.Return;
import com.pharmaops.sales.domain.model.Sale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSaleEventPublisher implements SaleEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // injetado pelo Spring Boot (já tem JavaTimeModule)

    @Value("${pharmaops.kafka.topics.sales-completed}")
    private String salesCompletedTopic;

    @Value("${pharmaops.kafka.topics.returns-processed}")
    private String returnsProcessedTopic;

    @Override
    public void publishSaleCompleted(Sale sale) {
        List<SaleItemPayload> items = sale.getItems().stream()
                .map(i -> new SaleItemPayload(
                        i.getProductId().toString(),
                        i.getBatchId().toString(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .toList();

        SaleCompletedPayload payload = new SaleCompletedPayload(
                sale.getId().toString(),
                sale.getStoreId().toString(),
                sale.getUserId().toString(),
                sale.getResponsiblePharmacistId() != null ? sale.getResponsiblePharmacistId().toString() : null,
                sale.isForced(),
                items,
                sale.getTotal());

        publish(salesCompletedTopic, sale.getStoreId().toString(),
                "SaleCompleted", sale.getCorrelationId().toString(), payload);

        log.info("Published SaleCompleted: saleId={} storeId={} total={}",
                sale.getId(), sale.getStoreId(), sale.getTotal());
    }

    @Override
    public void publishReturnProcessed(Return returnRecord, UUID storeId) {
        ReturnProcessedPayload payload = new ReturnProcessedPayload(
                returnRecord.getId().toString(),
                returnRecord.getSaleId().toString(),
                storeId.toString(),
                returnRecord.getUserId().toString(),
                returnRecord.getResponsiblePharmacistId() != null ? returnRecord.getResponsiblePharmacistId().toString() : null,
                returnRecord.getProductId().toString(),
                returnRecord.getBatchId().toString(),
                returnRecord.getQuantity(),
                returnRecord.getReason());

        publish(returnsProcessedTopic, storeId.toString(),
                "ReturnProcessed", returnRecord.getCorrelationId().toString(), payload);

        log.info("Published ReturnProcessed: returnId={} saleId={} storeId={}",
                returnRecord.getId(), returnRecord.getSaleId(), storeId);
    }

    private void publish(String topic, String key, String eventType, String correlationId, Object payload) {
        try {
            KafkaEventEnvelope envelope = new KafkaEventEnvelope(
                    UUID.randomUUID().toString(),
                    eventType,
                    Instant.now().toString(),
                    correlationId,
                    "1.0",
                    payload);

            String json = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(topic, key, json);
        } catch (JacksonException e) {
            // JacksonException é unchecked no Jackson 3.x — capturamos para logar corretamente
            log.error("Failed to serialize {} event: {}", eventType, e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }

    // Envelope padrão de todos os eventos Kafka do projeto
    record KafkaEventEnvelope(
            String eventId,
            String eventType,
            String timestamp,
            String correlationId,
            String version,
            Object payload) {}

    record SaleItemPayload(String productId, String batchId, int quantity, BigDecimal unitPrice) {}

    record SaleCompletedPayload(
            String saleId,
            String storeId,
            String userId,
            String responsiblePharmacistId,
            boolean forced,
            List<SaleItemPayload> items,
            BigDecimal total) {}

    record ReturnProcessedPayload(
            String returnId,
            String saleId,
            String storeId,
            String userId,
            String responsiblePharmacistId,
            String productId,
            String batchId,
            int quantity,
            String reason) {}
}
