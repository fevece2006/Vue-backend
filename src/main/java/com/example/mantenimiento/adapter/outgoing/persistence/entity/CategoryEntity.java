package com.example.mantenimiento.adapter.outgoing.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Schema(name = "Category", description = "Categoría de productos")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(example = "58fa5de6-b194-4e7d-814a-f0ed9072d8f3")
    private UUID id;
    @Schema(example = "Electrónica")
    private String name;

    public CategoryEntity() {
    }

    public CategoryEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

