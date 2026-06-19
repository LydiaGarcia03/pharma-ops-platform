package com.pharmaops.sales.infrastructure.config;

import com.pharmaops.sales.application.port.in.CreateSaleUseCase;
import com.pharmaops.sales.application.port.in.GetSaleUseCase;
import com.pharmaops.sales.application.port.in.ProcessReturnUseCase;
import com.pharmaops.sales.application.port.out.InventoryReservationPort;
import com.pharmaops.sales.application.port.out.ReturnRepository;
import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.application.port.out.SaleRepository;
import com.pharmaops.sales.application.usecase.CreateSaleUseCaseImpl;
import com.pharmaops.sales.application.usecase.GetSaleUseCaseImpl;
import com.pharmaops.sales.application.usecase.ProcessReturnUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateSaleUseCase createSaleUseCase(SaleRepository saleRepository,
                                               InventoryReservationPort inventoryReservationPort,
                                               SaleEventPublisher eventPublisher) {
        return new CreateSaleUseCaseImpl(saleRepository, inventoryReservationPort, eventPublisher);
    }

    @Bean
    public GetSaleUseCase getSaleUseCase(SaleRepository saleRepository) {
        return new GetSaleUseCaseImpl(saleRepository);
    }

    @Bean
    public ProcessReturnUseCase processReturnUseCase(SaleRepository saleRepository,
                                                     ReturnRepository returnRepository,
                                                     SaleEventPublisher eventPublisher) {
        return new ProcessReturnUseCaseImpl(saleRepository, returnRepository, eventPublisher);
    }
}
