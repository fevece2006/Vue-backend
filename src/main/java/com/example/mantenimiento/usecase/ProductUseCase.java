package com.example.mantenimiento.usecase;

import com.example.mantenimiento.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductUseCase {
    List<Product> list();

    Product create(Product product);

    Product update(UUID id, Product product);

    Optional<Product> getById(UUID id);

    void deleteById(UUID id);
}
