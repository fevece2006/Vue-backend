package com.example.mantenimiento.adapter.outgoing.persistence.mapper;

import com.example.mantenimiento.domain.model.User;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return User.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .role(entity.getRole())
            .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        
        return new UserEntity(
            domain.getId(),
            domain.getUsername(),
            domain.getPassword(),
            domain.getRole()
        );
    }
}
