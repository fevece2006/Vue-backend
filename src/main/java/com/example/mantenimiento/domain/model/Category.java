package com.example.mantenimiento.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;

public class Category {
    private final UUID id;
    private final String name;

    private Category(Builder builder) {
        this.id = builder.id;
        this.name = validateAndGetName(builder.name);
    }

    private String validateAndGetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.CATEGORY_NAME_REQUIRED);
        }
        if (name.length() > ValidationRules.MAX_CATEGORY_NAME_LENGTH) {
            throw new ValidationException(ErrorMessages.CATEGORY_NAME_MAX_LENGTH);
        }
        return name.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category withUpdatedName(String name) {
        return Category.builder()
            .id(this.id)
            .name(name)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
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

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}

