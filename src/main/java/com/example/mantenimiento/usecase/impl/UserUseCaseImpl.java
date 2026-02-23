package com.example.mantenimiento.usecase.impl;

import com.example.mantenimiento.adapter.outgoing.persistence.UserRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import com.example.mantenimiento.adapter.outgoing.persistence.mapper.UserEntityMapper;
import com.example.mantenimiento.domain.model.User;
import com.example.mantenimiento.usecase.UserUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUseCaseImpl implements UserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper userEntityMapper;

    public UserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public User register(User user) {
        User userWithEncodedPassword = User.builder()
            .id(user.getId())
            .username(user.getUsername())
            .password(passwordEncoder.encode(user.getPassword()))
            .role(user.getRole())
            .build();
        
        UserEntity entity = userEntityMapper.toEntity(userWithEncodedPassword);
        UserEntity saved = userRepository.save(entity);
        return userEntityMapper.toDomain(saved);
    }
}
