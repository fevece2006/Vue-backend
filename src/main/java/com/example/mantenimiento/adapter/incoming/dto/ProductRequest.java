package com.example.mantenimiento.adapter.incoming.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "ProductRequest", description = "Datos de entrada para crear un producto")
public class ProductRequest {

    @Schema(example = "Mouse")
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = ValidationRules.MAX_PRODUCT_NAME_LENGTH, message = ErrorMessages.PRODUCT_NAME_MAX_LENGTH)
    private String name;

    @Schema(example = "Inalámbrico")
    @Size(max = ValidationRules.MAX_PRODUCT_DESCRIPTION_LENGTH, message = ErrorMessages.PRODUCT_DESCRIPTION_MAX_LENGTH)
    private String description;

    @Schema(example = "19.90")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal price;

    @Schema(example = "58fa5de6-b194-4e7d-814a-f0ed9072d8f3")
    @NotNull(message = "El categoryId es obligatorio")
    private UUID categoryId;

    public ProductRequest() {
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
