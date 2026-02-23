package com.example.mantenimiento.adapter.incoming.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mantenimiento.adapter.incoming.dto.ApiErrorResponse;
import com.example.mantenimiento.adapter.incoming.dto.UserRegisterRequest;
import com.example.mantenimiento.adapter.incoming.dto.UserResponse;
import com.example.mantenimiento.adapter.incoming.mapper.UserMapper;
import com.example.mantenimiento.domain.model.User;
import com.example.mantenimiento.usecase.UserUseCase;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints de usuarios")
public class UserController {
    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    public UserController(
        UserUseCase userUseCase,
        UserMapper userMapper
    ) {
        this.userUseCase = userUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con contraseña cifrada en BCrypt")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Datos del usuario a registrar",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserRegisterRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "ValidationError",
                        value = "{\"timestamp\":\"2026-02-18T18:30:01.143Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"La password debe tener entre 6 y 120 caracteres\",\"path\":\"/users/register\"}"
                    ),
                    @ExampleObject(
                        name = "MalformedJson",
                        value = "{\"timestamp\":\"2026-02-18T18:30:05.811Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Formato de solicitud inválido\",\"path\":\"/users/register\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userMapper.toNewDomain(request);
        User registered = userUseCase.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(registered));
    }
}

