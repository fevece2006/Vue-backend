package com.example.mantenimiento.domain.model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;

class CategoryTest {

    @Test
    void withUpdatedName_updatesNameAndPreservesId() {
        UUID id = UUID.randomUUID();
        Category original = Category.builder()
            .id(id)
            .name("Electrónica")
            .build();

        Category updated = original.withUpdatedName("  Accesorios  ");

        assertEquals(id, updated.getId());
        assertEquals("Accesorios", updated.getName());
    }

    @Test
    void build_createsCategory_whenNameIsValid() {
        Category category = Category.builder()
            .name("Electrónica")
            .build();

        assertEquals("Electrónica", category.getName());
    }

    @Test
    void build_trimsName_whenContainsSpaces() {
        Category category = Category.builder()
            .name("  Electrónica  ")
            .build();

        assertEquals("Electrónica", category.getName());
    }

    @Test
    void build_throwsValidationException_whenNameIsBlank() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Category.builder()
            .name("   ")
            .build());

        assertEquals(ErrorMessages.CATEGORY_NAME_REQUIRED, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenNameExceedsMaxLength() {
        ValidationException ex = assertThrows(ValidationException.class, () -> Category.builder()
            .name("a".repeat(ValidationRules.MAX_CATEGORY_NAME_LENGTH + 1))
            .build());

        assertEquals(ErrorMessages.CATEGORY_NAME_MAX_LENGTH, ex.getMessage());
    }
}
