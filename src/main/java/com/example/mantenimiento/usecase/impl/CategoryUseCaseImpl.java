package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.CategoryRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.CategoryEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.CategoryEntityMapper;
import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.exception.ResourceNotFoundException;
import com.example.mantenimiento.domain.model.Category;
import com.example.mantenimiento.usecase.CategoryUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryUseCaseImpl implements CategoryUseCase {
    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    public CategoryUseCaseImpl(CategoryRepository categoryRepository, CategoryEntityMapper categoryEntityMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryEntityMapper = categoryEntityMapper;
    }

    @Override
    public List<Category> list() {
        return categoryRepository.findAll()
            .stream()
            .map(categoryEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Category create(Category category) {
        CategoryEntity entity = categoryEntityMapper.toEntity(category);
        CategoryEntity saved = categoryRepository.save(entity);
        return categoryEntityMapper.toDomain(saved);
    }

    @Override
    public Category update(UUID id, Category category) {
        CategoryEntity existing = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND, id.toString()));
        
        Category updatedCategory = category.withUpdatedName(category.getName());
        
        CategoryEntity updatedEntity = categoryEntityMapper.toEntity(
            Category.builder()
                .id(existing.getId())
                .name(updatedCategory.getName())
                .build()
        );
        
        CategoryEntity saved = categoryRepository.save(updatedEntity);
        return categoryEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Category> getById(UUID id) {
        return categoryRepository.findById(id)
            .map(categoryEntityMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND, id.toString());
        }
        categoryRepository.deleteById(id);
    }
}
