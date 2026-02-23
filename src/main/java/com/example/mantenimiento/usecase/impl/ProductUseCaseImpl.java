package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.ProductRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.ProductEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.ProductEntityMapper;
import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.exception.ResourceNotFoundException;
import com.example.mantenimiento.domain.model.Product;
import com.example.mantenimiento.usecase.ProductUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductUseCaseImpl implements ProductUseCase {
    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductUseCaseImpl(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public List<Product> list() {
        return productRepository.findAll()
            .stream()
            .map(productEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Product create(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);
        ProductEntity saved = productRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public Product update(UUID id, Product product) {
        ProductEntity existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND, id.toString()));
        
        Product updatedProduct = product.withUpdatedData(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategoryId()
        );
        
        ProductEntity updatedEntity = productEntityMapper.toEntity(
            Product.builder()
                .id(existing.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .categoryId(updatedProduct.getCategoryId())
                .build()
        );
        
        ProductEntity saved = productRepository.save(updatedEntity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> getById(UUID id) {
        return productRepository.findById(id)
            .map(productEntityMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND, id.toString());
        }
        productRepository.deleteById(id);
    }
}
