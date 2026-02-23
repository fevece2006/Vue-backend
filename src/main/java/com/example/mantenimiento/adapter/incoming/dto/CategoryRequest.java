package com.example.mantenimiento.adapter.incoming.dto;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CategoryRequest", description = "Datos de entrada para crear una categoría")
public class CategoryRequest {

    @Schema(example = "Electrónica")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = ValidationRules.MAX_CATEGORY_NAME_LENGTH, message = ErrorMessages.CATEGORY_NAME_MAX_LENGTH)
    private String name;

    public CategoryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
