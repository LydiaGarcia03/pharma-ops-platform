package com.pharmaops.sales.infrastructure.graphql;

import com.pharmaops.sales.application.port.in.CreateSaleUseCase;
import com.pharmaops.sales.application.port.in.GetSaleUseCase;
import com.pharmaops.sales.application.port.in.ProcessReturnUseCase;
import com.pharmaops.sales.domain.model.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SaleResolver {

    private final CreateSaleUseCase createSaleUseCase;
    private final GetSaleUseCase getSaleUseCase;
    private final ProcessReturnUseCase processReturnUseCase;

    @QueryMapping
    public SaleResponse sale(@Argument String id) {
        return SaleResponse.from(getSaleUseCase.getSale(UUID.fromString(id)));
    }

    @MutationMapping
    public SaleResponse createSale(@Argument CreateSaleInput input) {
        List<CreateSaleUseCase.ItemCommand> items = input.items().stream()
                .map(i -> new CreateSaleUseCase.ItemCommand(
                        UUID.fromString(i.productId()),
                        i.controlled(),
                        i.quantity(),
                        new BigDecimal(i.unitPrice())
                ))
                .toList();

        Sale sale = createSaleUseCase.createSale(new CreateSaleUseCase.Command(
                UUID.fromString(input.storeId()),
                UUID.fromString(input.userId()),
                input.responsiblePharmacistId() != null
                        ? UUID.fromString(input.responsiblePharmacistId()) : null,
                input.forced(),
                items
        ));
        return SaleResponse.from(sale);
    }

    @MutationMapping
    public ReturnResponse processReturn(@Argument ProcessReturnInput input) {
        var result = processReturnUseCase.processReturn(new ProcessReturnUseCase.Command(
                UUID.fromString(input.saleId()),
                UUID.fromString(input.productId()),
                UUID.fromString(input.batchId()),
                UUID.fromString(input.userId()),
                input.responsiblePharmacistId() != null
                        ? UUID.fromString(input.responsiblePharmacistId()) : null,
                input.quantity(),
                input.reason()
        ));
        return ReturnResponse.from(result);
    }

    record CreateSaleInput(String storeId, String userId, String responsiblePharmacistId,
                           boolean forced, List<SaleItemInput> items) {}
    record SaleItemInput(String productId, boolean controlled, int quantity, String unitPrice) {}
    record ProcessReturnInput(String saleId, String productId, String batchId, String userId,
                              String responsiblePharmacistId, int quantity, String reason) {}
}
