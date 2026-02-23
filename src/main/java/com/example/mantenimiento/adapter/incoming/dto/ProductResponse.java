package com.example.mantenimiento.adapter.incoming.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "ProductResponse", description = "Datos de salida de un producto")
public class ProductResponse {

    @Schema(example = "a6da5d57-42d1-40af-a1de-f53b2f8f2d15")
    private UUID id;

    @Schema(example = "Mouse")
    private String name;

    @Schema(example = "Inalámbrico")
    private String description;

    @Schema(example = "19.90")
    private BigDecimal price;

    @Schema(example = "58fa5de6-b194-4e7d-814a-f0ed9072d8f3")
    private UUID categoryId;

    public ProductResponse() {
    }

    public ProductResponse(UUID id, String name, String description, BigDecimal price, UUID categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
