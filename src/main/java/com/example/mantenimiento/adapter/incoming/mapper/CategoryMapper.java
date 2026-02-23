package com.example.mantenimiento.adapter.incoming.mapper;

import com.example.mantenimiento.adapter.incoming.dto.CategoryRequest;
import com.example.mantenimiento.adapter.incoming.dto.CategoryResponse;
import com.example.mantenimiento.domain.model.Category;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryRequest request) {
        return Category.builder()
            .name(request.getName())
            .build();
    }

    public Category toNewDomain(CategoryRequest request) {
        return Category.builder()
            .id(UUID.randomUUID())
            .name(request.getName())
            .build();
    }

    public CategoryResponse toResponse(Category domain) {
        return new CategoryResponse(domain.getId(), domain.getName());
    }
}
