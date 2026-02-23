package com.example.mantenimiento.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;

class ProductTest {

    @Test
    void withUpdatedData_updatesFieldsAndPreservesId() {
        UUID id = UUID.randomUUID();
        UUID initialCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        Product original = Product.builder()
            .id(id)
            .name("Mouse")
            .description("Inalámbrico")
            .price(new BigDecimal("19.90"))
            .categoryId(initialCategoryId)
            .build();

        Product updated = original.withUpdatedData("  Teclado  ", "Mecánico", new BigDecimal("49.90"), newCategoryId);

        assertEquals(id, updated.getId());
        assertEquals("Teclado", updated.getName());
        assertEquals("Mecánico", updated.getDescription());
        assertEquals(new BigDecimal("49.90"), updated.getPrice());
        assertEquals(newCategoryId, updated.getCategoryId());
    }

    @Test
    void build_createsProduct_whenDataIsValid() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Product product = Product.builder()
            .id(id)
            .name("Mouse")
            .description("Inalámbrico")
            .price(new BigDecimal("19.90"))
            .categoryId(categoryId)
            .build();

        assertEquals(id, product.getId());
        assertEquals("Mouse", product.getName());
        assertEquals("Inalámbrico", product.getDescription());
        assertEquals(new BigDecimal("19.90"), product.getPrice());
        assertEquals(categoryId, product.getCategoryId());
    }

    @Test
    void build_trimsName_andAllowsNullDescription() {
        Product product = Product.builder()
            .name("  Mouse  ")
            .description(null)
            .price(new BigDecimal("10.00"))
            .categoryId(UUID.randomUUID())
            .build();

        assertEquals("Mouse", product.getName());
        assertNull(product.getDescription());
    }

    @Test
    void build_throwsValidationException_whenNameIsBlank() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Product.builder()
            .name("   ")
            .description("desc")
            .price(new BigDecimal("10.00"))
            .categoryId(UUID.randomUUID())
            .build());

        assertEquals(ErrorMessages.PRODUCT_NAME_REQUIRED, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenNameExceedsMaxLength() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Product.builder()
            .name("a".repeat(ValidationRules.MAX_PRODUCT_NAME_LENGTH + 1))
            .description("desc")
            .price(new BigDecimal("10.00"))
            .categoryId(UUID.randomUUID())
            .build());

        assertEquals(ErrorMessages.PRODUCT_NAME_MAX_LENGTH, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenDescriptionExceedsMaxLength() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Product.builder()
            .name("Mouse")
            .description("a".repeat(ValidationRules.MAX_PRODUCT_DESCRIPTION_LENGTH + 1))
            .price(new BigDecimal("10.00"))
            .categoryId(UUID.randomUUID())
            .build());

        assertEquals(ErrorMessages.PRODUCT_DESCRIPTION_MAX_LENGTH, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenPriceIsNullOrNotPositive() {
        ValidationException nullEx = assertThrows(ValidationException.class, () -> Product.builder()
            .name("Mouse")
            .description("desc")
            .price(null)
            .categoryId(UUID.randomUUID())
            .build());

        ValidationException zeroEx = assertThrows(ValidationException.class, () -> Product.builder()
            .name("Mouse")
            .description("desc")
            .price(BigDecimal.ZERO)
            .categoryId(UUID.randomUUID())
            .build());

        assertEquals(ErrorMessages.PRODUCT_PRICE_POSITIVE, nullEx.getMessage());
        assertEquals(ErrorMessages.PRODUCT_PRICE_POSITIVE, zeroEx.getMessage());
    }

    @Test
    void build_throwsValidationException_whenCategoryIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Product.builder()
            .name("Mouse")
            .description("desc")
            .price(new BigDecimal("10.00"))
            .categoryId(null)
            .build());

        assertEquals(ErrorMessages.PRODUCT_CATEGORY_REQUIRED, ex.getMessage());
    }
}
