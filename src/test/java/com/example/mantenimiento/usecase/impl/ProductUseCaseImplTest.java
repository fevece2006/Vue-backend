package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.ProductRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.ProductEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.ProductEntityMapper;
import com.example.mantenimiento.domain.exception.ResourceNotFoundException;
import com.example.mantenimiento.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    private ProductUseCaseImpl productUseCase;

    @BeforeEach
    void setUp() {
        productUseCase = new ProductUseCaseImpl(productRepository, productEntityMapper);
    }

    @Test
    void list_returnsAllProducts() {
        ProductEntity e1 = new ProductEntity(UUID.randomUUID(), "Mouse", "Inalámbrico", BigDecimal.valueOf(19.9), UUID.randomUUID());
        ProductEntity e2 = new ProductEntity(UUID.randomUUID(), "Teclado", "Mecánico", BigDecimal.valueOf(49.9), UUID.randomUUID());
        Product p1 = Product.builder().id(e1.getId()).name(e1.getName()).description(e1.getDescription()).price(e1.getPrice()).categoryId(e1.getCategoryId()).build();
        Product p2 = Product.builder().id(e2.getId()).name(e2.getName()).description(e2.getDescription()).price(e2.getPrice()).categoryId(e2.getCategoryId()).build();

        when(productRepository.findAll()).thenReturn(List.of(e1, e2));
        when(productEntityMapper.toDomain(e1)).thenReturn(p1);
        when(productEntityMapper.toDomain(e2)).thenReturn(p2);

        List<Product> result = productUseCase.list();
        assertEquals(2, result.size());
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p2));
    }

    @Test
    void create_savesProduct() {
        UUID catId = UUID.randomUUID();
        Product product = Product.builder().name("Webcam").description("Full HD").price(BigDecimal.valueOf(29.9)).categoryId(catId).build();
        ProductEntity entity = new ProductEntity(UUID.randomUUID(), "Webcam", "Full HD", BigDecimal.valueOf(29.9), catId);

        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productEntityMapper.toDomain(entity)).thenReturn(product);

        Product result = productUseCase.create(product);
        assertEquals(product, result);
    }

    @Test
    void getById_returnsProduct() {
        UUID id = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        ProductEntity entity = new ProductEntity(id, "Monitor", "27 pulgadas", BigDecimal.valueOf(199.9), catId);
        Product product = Product.builder().id(id).name("Monitor").description("27 pulgadas").price(BigDecimal.valueOf(199.9)).categoryId(catId).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productEntityMapper.toDomain(entity)).thenReturn(product);

        Optional<Product> result = productUseCase.getById(id);
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void update_updatesExistingProduct() {
        UUID id = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        ProductEntity existing = new ProductEntity(id, "Mouse", "Inalámbrico", BigDecimal.valueOf(19.9), catId);
        Product updates = Product.builder().name("Mouse Pro").description("Inalámbrico RGB").price(BigDecimal.valueOf(29.9)).categoryId(catId).build();
        ProductEntity updatedEntity = new ProductEntity(id, "Mouse Pro", "Inalámbrico RGB", BigDecimal.valueOf(29.9), catId);
        Product updatedProduct = Product.builder().id(id).name("Mouse Pro").description("Inalámbrico RGB").price(BigDecimal.valueOf(29.9)).categoryId(catId).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productEntityMapper.toEntity(any(Product.class))).thenReturn(updatedEntity);
        when(productRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(productEntityMapper.toDomain(updatedEntity)).thenReturn(updatedProduct);

        Product result = productUseCase.update(id, updates);
        assertEquals("Mouse Pro", result.getName());
        assertEquals("Inalámbrico RGB", result.getDescription());
        assertEquals(BigDecimal.valueOf(29.9), result.getPrice());
        assertEquals(catId, result.getCategoryId());
    }

    @Test
    void update_throwsResourceNotFoundExceptionWhenProductDoesNotExist() {
        UUID id = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        Product updates = Product.builder().name("Mouse Pro").description("Inalámbrico RGB").price(BigDecimal.valueOf(29.9)).categoryId(catId).build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productUseCase.update(id, updates));
    }

    @Test
    void deleteById_throwsResourceNotFoundExceptionWhenProductDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productUseCase.deleteById(id));
    }
}
