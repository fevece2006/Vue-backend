package com.example.mantenimiento.domain.model;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void build_setsDefaultRole_whenRoleIsNull() {
        User user = User.builder()
            .username("newuser")
            .password("secret1")
            .role(null)
            .build();

        assertEquals(ValidationRules.DEFAULT_USER_ROLE, user.getRole());
    }

    @Test
    void build_trimsUsernameAndRole_whenValuesContainSpaces() {
        User user = User.builder()
            .username("  newuser  ")
            .password("secret1")
            .role("  ROLE_ADMIN  ")
            .build();

        assertEquals("newuser", user.getUsername());
        assertEquals("ROLE_ADMIN", user.getRole());
    }

    @Test
    void build_throwsValidationException_whenUsernameIsBlank() {
        ValidationException ex = assertThrows(ValidationException.class, () -> User.builder()
            .username("   ")
            .password("secret1")
            .role("ROLE_USER")
            .build());

        assertEquals(ErrorMessages.USER_USERNAME_REQUIRED, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenUsernameLengthIsOutOfRange() {
        ValidationException shortEx = assertThrows(ValidationException.class, () -> User.builder()
            .username("ab")
            .password("secret1")
            .role("ROLE_USER")
            .build());

        ValidationException longEx = assertThrows(ValidationException.class, () -> User.builder()
            .username("a".repeat(51))
            .password("secret1")
            .role("ROLE_USER")
            .build());

        assertEquals(ErrorMessages.USER_USERNAME_LENGTH, shortEx.getMessage());
        assertEquals(ErrorMessages.USER_USERNAME_LENGTH, longEx.getMessage());
    }

    @Test
    void build_throwsValidationException_whenPasswordIsBlank() {
        ValidationException ex = assertThrows(ValidationException.class, () -> User.builder()
            .username("newuser")
            .password("   ")
            .role("ROLE_USER")
            .build());

        assertEquals(ErrorMessages.USER_PASSWORD_REQUIRED, ex.getMessage());
    }

    @Test
    void build_throwsValidationException_whenPasswordLengthIsOutOfRange() {
        ValidationException shortEx = assertThrows(ValidationException.class, () -> User.builder()
            .username("newuser")
            .password("12345")
            .role("ROLE_USER")
            .build());

        ValidationException longEx = assertThrows(ValidationException.class, () -> User.builder()
            .username("newuser")
            .password("a".repeat(121))
            .role("ROLE_USER")
            .build());

        assertEquals(ErrorMessages.USER_PASSWORD_LENGTH, shortEx.getMessage());
        assertEquals(ErrorMessages.USER_PASSWORD_LENGTH, longEx.getMessage());
    }

    @Test
    void build_throwsValidationException_whenRoleHasInvalidFormat() {
        ValidationException ex = assertThrows(ValidationException.class, () -> User.builder()
            .username("newuser")
            .password("secret1")
            .role("admin")
            .build());

        assertEquals(ErrorMessages.USER_ROLE_INVALID_FORMAT, ex.getMessage());
    }
}
