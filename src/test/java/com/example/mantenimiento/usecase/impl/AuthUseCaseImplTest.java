package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.UserRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import com.example.mantenimiento.config.JwtUtil;
import com.example.mantenimiento.usecase.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthUseCaseImpl authUseCase;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCaseImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void authenticate_returnsToken_whenCredentialsAreValid() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "admin", "hashed", "ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("jwt-token");

        String result = authUseCase.authenticate("admin", "password");
        assertEquals("jwt-token", result);
    }

    @Test
    void authenticate_throwsInvalidCredentials_whenPasswordDoesNotMatch() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "admin", "hashed", "ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, 
            () -> authUseCase.authenticate("admin", "wrong-password"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void authenticate_throwsInvalidCredentials_whenUserDoesNotExist() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, 
            () -> authUseCase.authenticate("unknown", "password"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }
}
