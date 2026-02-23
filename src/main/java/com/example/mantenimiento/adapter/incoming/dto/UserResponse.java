package com.example.mantenimiento.adapter.incoming.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "UserResponse", description = "Datos de salida de un usuario registrado")
public class UserResponse {

    @Schema(example = "3c00f8ed-1118-4f58-9bc5-1b467faec41d")
    private UUID id;

    @Schema(example = "newuser")
    private String username;

    @Schema(example = "ROLE_USER")
    private String role;

    public UserResponse() {
    }

    public UserResponse(UUID id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
