package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.application.port.in.CreateProductUseCase;
import com.pharmaops.inventory.domain.model.Product;
import com.pharmaops.inventory.application.port.out.ProductRepository;
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
public class ProductResolver {

    private final CreateProductUseCase createProductUseCase;
    private final ProductRepository productRepository;

    @QueryMapping
    public List<ProductResponse> products() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }

    @QueryMapping
    public ProductResponse product(@Argument String id) {
        return productRepository.findById(UUID.fromString(id))
                .map(ProductResponse::from)
                .orElse(null);
    }

    @MutationMapping
    public ProductResponse createProduct(@Argument CreateProductInput input) {
        Product product = createProductUseCase.createProduct(
                new CreateProductUseCase.Command(
                        input.name(),
                        input.barcode(),
                        input.controlled(),
                        new BigDecimal(input.salePrice().replace(",", "."))
                )
        );
        return ProductResponse.from(product);
    }

    record CreateProductInput(String name, String barcode, boolean controlled, String salePrice) {}
}
