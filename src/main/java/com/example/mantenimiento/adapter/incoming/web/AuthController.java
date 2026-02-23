package com.example.mantenimiento.adapter.incoming.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.mantenimiento.adapter.incoming.dto.AuthRequest;
import com.example.mantenimiento.adapter.incoming.dto.AuthResponse;
import com.example.mantenimiento.adapter.incoming.dto.ApiErrorResponse;
import com.example.mantenimiento.usecase.AuthUseCase;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Auth", description = "Endpoints de autenticación")
public class AuthController {
    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y retorna token JWT")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Credenciales del usuario",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))
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
                        value = "{\"timestamp\":\"2026-02-18T18:25:43.511Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"El username es obligatorio\",\"path\":\"/login\"}"
                    ),
                    @ExampleObject(
                        name = "MalformedJson",
                        value = "{\"timestamp\":\"2026-02-18T18:25:49.203Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Formato de solicitud inválido\",\"path\":\"/login\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = "{\"timestamp\":\"2026-02-18T18:26:10.174Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Credenciales inválidas\",\"path\":\"/login\"}"
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authUseCase.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

