package com.example.mantenimiento.domain.model;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.domain.constants.ValidationRules;
import com.example.mantenimiento.domain.exception.ValidationException;

public class User {
    private final UUID id;
    private final String username;
    private final String password;
    private final String role;

    private User(Builder builder) {
        this.id = builder.id;
        this.username = validateAndGetUsername(builder.username);
        this.password = validateAndGetPassword(builder.password);
        this.role = validateAndGetRole(builder.role);
    }

    private String validateAndGetUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.USER_USERNAME_REQUIRED);
        }
        String normalizedUsername = username.trim();
        if (normalizedUsername.length() < ValidationRules.MIN_USERNAME_LENGTH ||
            normalizedUsername.length() > ValidationRules.MAX_USERNAME_LENGTH) {
            throw new ValidationException(ErrorMessages.USER_USERNAME_LENGTH);
        }
        return normalizedUsername;
    }

    private String validateAndGetPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.USER_PASSWORD_REQUIRED);
        }
        if (password.length() < ValidationRules.MIN_PASSWORD_LENGTH ||
            password.length() > ValidationRules.MAX_PASSWORD_LENGTH) {
            throw new ValidationException(ErrorMessages.USER_PASSWORD_LENGTH);
        }
        return password;
    }

    private String validateAndGetRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return ValidationRules.DEFAULT_USER_ROLE;
        }
        String normalizedRole = role.trim();
        if (!Pattern.matches(ValidationRules.USER_ROLE_PATTERN, normalizedRole)) {
            throw new ValidationException(ErrorMessages.USER_ROLE_INVALID_FORMAT);
        }
        return normalizedRole;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String username;
        private String password;
        private String role;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

