package com.example.mantenimiento.adapter.incoming.mapper;

import com.example.mantenimiento.adapter.incoming.dto.UserRegisterRequest;
import com.example.mantenimiento.adapter.incoming.dto.UserResponse;
import com.example.mantenimiento.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    public User toNewDomain(UserRegisterRequest request) {
        return User.builder()
            .id(UUID.randomUUID())
            .username(request.getUsername())
            .password(request.getPassword())
            .role(request.getRole())
            .build();
    }

    public UserResponse toResponse(User domain) {
        return new UserResponse(domain.getId(), domain.getUsername(), domain.getRole());
    }
}
