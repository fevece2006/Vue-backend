package com.example.mantenimiento.adapter.incoming.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "AuthRequest", description = "Credenciales para autenticación")
public class AuthRequest {
    @Schema(example = "admin")
    @NotBlank(message = "El username es obligatorio")
    private String username;
    @Schema(example = "password")
    @NotBlank(message = "La password es obligatoria")
    private String password;

    public AuthRequest() {
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
}

