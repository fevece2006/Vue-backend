package com.example.mantenimiento.adapter.incoming.web;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mantenimiento.adapter.incoming.dto.ApiErrorResponse;
import com.example.mantenimiento.adapter.incoming.dto.CategoryRequest;
import com.example.mantenimiento.adapter.incoming.dto.CategoryResponse;
import com.example.mantenimiento.adapter.incoming.mapper.CategoryMapper;
import com.example.mantenimiento.usecase.CategoryUseCase;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Gestión de categorías")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryUseCase categoryUseCase;
    private final CategoryMapper categoryMapper;

    public CategoryController(
        CategoryUseCase categoryUseCase,
        CategoryMapper categoryMapper
    ) {
        this.categoryUseCase = categoryUseCase;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar categorías")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado de categorías",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "MissingToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:10.051Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/categories\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:15.174Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/categories\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<List<CategoryResponse>> list() {
        List<CategoryResponse> categories = categoryUseCase.list()
            .stream()
            .map(categoryMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear categoría")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Datos de la categoría a crear",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Categoría creada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryResponse.class))
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
                        value = "{\"timestamp\":\"2026-02-18T18:31:44.728Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"El nombre de la categoría es obligatorio\",\"path\":\"/categories\"}"
                    ),
                    @ExampleObject(
                        name = "MalformedJson",
                        value = "{\"timestamp\":\"2026-02-18T18:31:50.321Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Formato de solicitud inválido\",\"path\":\"/categories\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "MissingToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:10.051Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/categories\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:15.174Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/categories\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryMapper.toResponse(
            categoryUseCase.create(categoryMapper.toNewDomain(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar categoría")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Datos de la categoría a actualizar",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categoría actualizada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryMapper.toResponse(
            categoryUseCase.update(id, categoryMapper.toDomain(request))
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Obtener categoría por ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categoría encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "MissingToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:10.051Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:15.174Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(
                    name = "NotFound",
                    value = "{\"timestamp\":\"2026-02-18T18:32:33.601Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Categoría no encontrada\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                )
            )
        )
    })
    public ResponseEntity<CategoryResponse> get(@PathVariable UUID id) {
        return categoryUseCase.getById(id)
            .map(category -> ResponseEntity.ok(categoryMapper.toResponse(category)))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Eliminar categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "MissingToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:10.051Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:31:15.174Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(
                    name = "NotFound",
                    value = "{\"timestamp\":\"2026-02-18T18:32:33.601Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Categoría no encontrada\",\"path\":\"/categories/58fa5de6-b194-4e7d-814a-f0ed9072d8f3\"}"
                )
            )
        )
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

