package com.example.mantenimiento.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;

public class Product {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final UUID categoryId;

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = validateAndGetName(builder.name);
        this.description = validateAndGetDescription(builder.description);
        this.price = validateAndGetPrice(builder.price);
        this.categoryId = validateAndGetCategoryId(builder.categoryId);
    }

    private String validateAndGetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PRODUCT_NAME_REQUIRED);
        }
        if (name.length() > ValidationRules.MAX_PRODUCT_NAME_LENGTH) {
            throw new ValidationException(ErrorMessages.PRODUCT_NAME_MAX_LENGTH);
        }
        return name.trim();
    }

    private String validateAndGetDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() > ValidationRules.MAX_PRODUCT_DESCRIPTION_LENGTH) {
            throw new ValidationException(ErrorMessages.PRODUCT_DESCRIPTION_MAX_LENGTH);
        }
        return description;
    }

    private BigDecimal validateAndGetPrice(BigDecimal price) {
        if (price == null || price.compareTo(ValidationRules.MIN_PRICE) <= 0) {
            throw new ValidationException(ErrorMessages.PRODUCT_PRICE_POSITIVE);
        }
        return price;
    }

    private UUID validateAndGetCategoryId(UUID categoryId) {
        if (categoryId == null) {
            throw new ValidationException(ErrorMessages.PRODUCT_CATEGORY_REQUIRED);
        }
        return categoryId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public Product withUpdatedData(String name, String description, BigDecimal price, UUID categoryId) {
        return Product.builder()
            .id(this.id)
            .name(name)
            .description(description)
            .price(price)
            .categoryId(categoryId)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
        private UUID categoryId;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder categoryId(UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
