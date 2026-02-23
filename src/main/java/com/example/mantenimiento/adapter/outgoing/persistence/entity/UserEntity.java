package com.example.mantenimiento.adapter.outgoing.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
@Schema(name = "User", description = "Usuario de la aplicación")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(example = "3c00f8ed-1118-4f58-9bc5-1b467faec41d")
    private UUID id;
    @Schema(example = "admin")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(example = "$2a$10$CJUMQ7f5I4CyN8rD9OQ6mOe1f4akf0X4m2sTfnP0AcN0TyM4Igx3i", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    @Schema(example = "ROLE_USER")
    private String role;

    public UserEntity() {
    }

    public UserEntity(UUID id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
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

