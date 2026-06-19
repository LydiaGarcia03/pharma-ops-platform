package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.application.port.in.AddBatchUseCase;
import com.pharmaops.inventory.domain.model.Batch;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BatchResolver {

    private final AddBatchUseCase addBatchUseCase;

    @MutationMapping
    public BatchResponse addBatch(@Argument AddBatchInput input) {
        Batch batch = addBatchUseCase.addBatch(
                new AddBatchUseCase.Command(
                        UUID.fromString(input.productId()),
                        input.batchNumber(),
                        LocalDate.parse(input.expirationDate()),
                        input.initialQuantity()
                )
        );
        return BatchResponse.from(batch);
    }

    record AddBatchInput(String productId, String batchNumber, String expirationDate, int initialQuantity) {}
}
