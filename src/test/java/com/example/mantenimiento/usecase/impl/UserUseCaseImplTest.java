package com.example.mantenimiento.usecase.impl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.mantenimiento.adapter.outgoing.persistence.UserRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.UserEntityMapper;
import com.example.mantenimiento.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEntityMapper userEntityMapper;

    private UserUseCaseImpl userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCaseImpl(userRepository, passwordEncoder, userEntityMapper);
    }

    @Test
    void register_encryptsPasswordAndSetsDefaultRole_whenRoleIsNull() {
        UUID id = UUID.randomUUID();
        User input = User.builder().id(id).username("newuser").password("plain12").role(null).build();
        UserEntity entity = new UserEntity(id, "newuser", "hashed", "ROLE_USER");
        User savedUser = User.builder().id(id).username("newuser").password("hashed").role("ROLE_USER").build();

        when(passwordEncoder.encode("plain12")).thenReturn("hashed");
        when(userEntityMapper.toEntity(any(User.class))).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(savedUser);

        User result = userUseCase.register(input);
        
        assertEquals("hashed", result.getPassword());
        assertEquals("ROLE_USER", result.getRole());
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void register_keepsProvidedRole_whenRoleHasValue() {
        UUID id = UUID.randomUUID();
        User input = User.builder().id(id).username("admin").password("plain12").role("ROLE_ADMIN").build();
        UserEntity entity = new UserEntity(id, "admin", "hashed-admin", "ROLE_ADMIN");
        User savedUser = User.builder().id(id).username("admin").password("hashed-admin").role("ROLE_ADMIN").build();

        when(passwordEncoder.encode("plain12")).thenReturn("hashed-admin");
        when(userEntityMapper.toEntity(any(User.class))).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(savedUser);

        User result = userUseCase.register(input);
        
        assertEquals("hashed-admin", result.getPassword());
        assertEquals("ROLE_ADMIN", result.getRole());
    }
}
