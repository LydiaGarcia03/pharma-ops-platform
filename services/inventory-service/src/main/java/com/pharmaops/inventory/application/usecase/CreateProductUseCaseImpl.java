package com.pharmaops.inventory.application.usecase;

import com.pharmaops.inventory.application.port.in.CreateProductUseCase;
import com.pharmaops.inventory.application.port.out.ProductRepository;
import com.pharmaops.inventory.domain.exception.ProductAlreadyExistsException;
import com.pharmaops.inventory.domain.model.Product;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Command command) {
        if (productRepository.existsByBarcode(command.barcode())) {
            throw new ProductAlreadyExistsException(command.barcode());
        }
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(command.name())
                .barcode(command.barcode())
                .controlled(command.controlled())
                .salePrice(command.salePrice())
                .active(true)
                .build();
        return productRepository.save(product);
    }
}
