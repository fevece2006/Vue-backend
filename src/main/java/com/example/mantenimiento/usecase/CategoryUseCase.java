package com.example.mantenimiento.usecase;

import com.example.mantenimiento.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryUseCase {
    List<Category> list();

    Category create(Category category);

    Category update(UUID id, Category category);

    Optional<Category> getById(UUID id);

    void deleteById(UUID id);
}
