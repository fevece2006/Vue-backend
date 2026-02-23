package com.example.mantenimiento.adapter.incoming.mapper;

import com.example.mantenimiento.adapter.incoming.dto.ProductRequest;
import com.example.mantenimiento.adapter.incoming.dto.ProductResponse;
import com.example.mantenimiento.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductMapper {

    public Product toDomain(ProductRequest request) {
        return Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .categoryId(request.getCategoryId())
            .build();
    }

    public Product toNewDomain(ProductRequest request) {
        return Product.builder()
            .id(UUID.randomUUID())
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .categoryId(request.getCategoryId())
            .build();
    }

    public ProductResponse toResponse(Product domain) {
        return new ProductResponse(
            domain.getId(),
            domain.getName(),
            domain.getDescription(),
            domain.getPrice(),
            domain.getCategoryId()
        );
    }
}
