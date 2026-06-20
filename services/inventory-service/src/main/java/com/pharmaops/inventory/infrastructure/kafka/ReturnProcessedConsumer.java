package com.pharmaops.inventory.infrastructure.kafka;

import com.pharmaops.inventory.application.port.in.ProcessReturnUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReturnProcessedConsumer {

    private final ProcessReturnUseCase processReturn;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${pharmaops.kafka.topics.returns-processed}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            JsonNode root = objectMapper.readTree(record.value());
            JsonNode payload = root.get("payload");
            String correlationId = root.path("correlationId").asText();

            UUID productId  = UUID.fromString(payload.get("productId").asText());
            UUID storeId    = UUID.fromString(payload.get("storeId").asText());
            UUID batchId    = UUID.fromString(payload.get("batchId").asText());
            int  quantity   = payload.get("quantity").asInt();

            log.info("Processing ReturnProcessed: productId={} storeId={} batchId={} qty={} correlationId={}",
                    productId, storeId, batchId, quantity, correlationId);

            processReturn.processReturn(new ProcessReturnUseCase.Command(
                    productId, storeId, batchId, quantity, UUID.fromString(correlationId)));

            log.info("ReturnProcessed handled: stock credited back for productId={} storeId={}", productId, storeId);

        } catch (Exception e) {
            log.error("Failed to process ReturnProcessed event at offset={}: {}",
                    record.offset(), e.getMessage(), e);
        }
    }
}
