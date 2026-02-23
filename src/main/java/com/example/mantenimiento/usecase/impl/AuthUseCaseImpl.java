package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.UserRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import com.example.mantenimiento.config.JwtUtil;
import com.example.mantenimiento.domain.constants.ErrorMessages;
import com.example.mantenimiento.usecase.AuthUseCase;
import com.example.mantenimiento.usecase.exception.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCaseImpl implements AuthUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String authenticate(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException(ErrorMessages.INVALID_CREDENTIALS));
        
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new InvalidCredentialsException(ErrorMessages.INVALID_CREDENTIALS);
        }
        
        return jwtUtil.generateToken(userEntity.getUsername(), userEntity.getRole());
    }
}

