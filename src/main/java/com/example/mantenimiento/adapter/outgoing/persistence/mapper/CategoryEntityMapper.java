package com.example.mantenimiento.adapter.outgoing.persistence.mapper;

import com.example.mantenimiento.domain.model.Category;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Category.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }
        
        return new CategoryEntity(
            domain.getId(),
            domain.getName()
        );
    }
}
