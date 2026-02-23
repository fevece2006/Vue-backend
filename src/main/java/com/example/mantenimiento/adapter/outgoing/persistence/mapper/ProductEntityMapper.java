package com.example.mantenimiento.adapter.outgoing.persistence.mapper;

import com.example.mantenimiento.domain.model.Product;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Product.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .price(entity.getPrice())
            .categoryId(entity.getCategoryId())
            .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        
        return new ProductEntity(
            domain.getId(),
            domain.getName(),
            domain.getDescription(),
            domain.getPrice(),
            domain.getCategoryId()
        );
    }
}
