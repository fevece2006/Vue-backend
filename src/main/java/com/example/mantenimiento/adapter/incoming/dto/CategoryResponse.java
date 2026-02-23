package com.example.mantenimiento.adapter.incoming.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "CategoryResponse", description = "Datos de salida de una categoría")
public class CategoryResponse {

    @Schema(example = "58fa5de6-b194-4e7d-814a-f0ed9072d8f3")
    private UUID id;

    @Schema(example = "Electrónica")
    private String name;

    public CategoryResponse() {
    }

    public CategoryResponse(UUID id, String name) {
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
