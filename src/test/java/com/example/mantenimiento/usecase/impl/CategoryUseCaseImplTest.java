package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.CategoryRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.CategoryEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.CategoryEntityMapper;
import com.example.mantenimiento.domain.exception.ResourceNotFoundException;
import com.example.mantenimiento.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEntityMapper categoryEntityMapper;

    private CategoryUseCaseImpl categoryUseCase;

    @BeforeEach
    void setUp() {
        categoryUseCase = new CategoryUseCaseImpl(categoryRepository, categoryEntityMapper);
    }

    @Test
    void list_returnsAllCategories() {
        CategoryEntity e1 = new CategoryEntity(UUID.randomUUID(), "Electrónica");
        CategoryEntity e2 = new CategoryEntity(UUID.randomUUID(), "Hogar");
        Category c1 = Category.builder().id(e1.getId()).name(e1.getName()).build();
        Category c2 = Category.builder().id(e2.getId()).name(e2.getName()).build();

        when(categoryRepository.findAll()).thenReturn(List.of(e1, e2));
        when(categoryEntityMapper.toDomain(e1)).thenReturn(c1);
        when(categoryEntityMapper.toDomain(e2)).thenReturn(c2);

        List<Category> result = categoryUseCase.list();
        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));
    }

    @Test
    void create_savesCategory() {
        Category category = Category.builder().name("Gaming").build();
        CategoryEntity entity = new CategoryEntity(UUID.randomUUID(), "Gaming");

        when(categoryEntityMapper.toEntity(category)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

        Category result = categoryUseCase.create(category);
        assertEquals(category, result);
    }

    @Test
    void getById_returnsCategory() {
        UUID id = UUID.randomUUID();
        CategoryEntity entity = new CategoryEntity(id, "Audio");
        Category category = Category.builder().id(id).name("Audio").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

        Optional<Category> result = categoryUseCase.getById(id);
        assertTrue(result.isPresent());
        assertEquals(category, result.get());
    }

    @Test
    void update_updatesExistingCategory() {
        UUID id = UUID.randomUUID();
        CategoryEntity existing = new CategoryEntity(id, "Electrónica");
        Category updates = Category.builder().name("Tecnología").build();
        CategoryEntity updatedEntity = new CategoryEntity(id, "Tecnología");
        Category updatedCategory = Category.builder().id(id).name("Tecnología").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryEntityMapper.toEntity(any(Category.class))).thenReturn(updatedEntity);
        when(categoryRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(categoryEntityMapper.toDomain(updatedEntity)).thenReturn(updatedCategory);

        Category result = categoryUseCase.update(id, updates);
        assertEquals("Tecnología", result.getName());
    }

    @Test
    void update_throwsResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();
        Category updates = Category.builder().name("Tecnología").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryUseCase.update(id, updates));
    }

    @Test
    void deleteById_throwsResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryUseCase.deleteById(id));
    }
}
