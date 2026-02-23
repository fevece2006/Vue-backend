package com.example.mantenimiento.adapter.incoming.dto;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "UserRegisterRequest", description = "Datos de entrada para registrar un usuario")
public class UserRegisterRequest {

    @Schema(example = "newuser")
    @NotBlank(message = ErrorMessages.USER_USERNAME_REQUIRED)
    @Size(
        min = ValidationRules.MIN_USERNAME_LENGTH,
        max = ValidationRules.MAX_USERNAME_LENGTH,
        message = ErrorMessages.USER_USERNAME_LENGTH
    )
    private String username;

    @Schema(example = "mySecurePassword")
    @NotBlank(message = ErrorMessages.USER_PASSWORD_REQUIRED)
    @Size(
        min = ValidationRules.MIN_PASSWORD_LENGTH,
        max = ValidationRules.MAX_PASSWORD_LENGTH,
        message = ErrorMessages.USER_PASSWORD_LENGTH
    )
    private String password;

    @Schema(example = "ROLE_USER")
    @Pattern(regexp = ValidationRules.USER_ROLE_PATTERN, message = ErrorMessages.USER_ROLE_INVALID_FORMAT)
    private String role;

    public UserRegisterRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
