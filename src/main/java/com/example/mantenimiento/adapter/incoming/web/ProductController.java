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
import com.example.mantenimiento.adapter.incoming.dto.ProductRequest;
import com.example.mantenimiento.adapter.incoming.dto.ProductResponse;
import com.example.mantenimiento.adapter.incoming.mapper.ProductMapper;
import com.example.mantenimiento.usecase.ProductUseCase;

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
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestión de productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;

    public ProductController(
        ProductUseCase productUseCase,
        ProductMapper productMapper
    ) {
        this.productUseCase = productUseCase;
        this.productMapper = productMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar productos")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado de productos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))
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
                        value = "{\"timestamp\":\"2026-02-18T18:33:10.932Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/products\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:33:16.817Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/products\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<List<ProductResponse>> list() {
        List<ProductResponse> products = productUseCase.list()
            .stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear producto")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Datos del producto a crear",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))
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
                        value = "{\"timestamp\":\"2026-02-18T18:33:48.442Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"El precio debe ser mayor que cero\",\"path\":\"/products\"}"
                    ),
                    @ExampleObject(
                        name = "MalformedJson",
                        value = "{\"timestamp\":\"2026-02-18T18:33:53.117Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Formato de solicitud inválido\",\"path\":\"/products\"}"
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
                        value = "{\"timestamp\":\"2026-02-18T18:33:10.932Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/products\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:33:16.817Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/products\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productMapper.toResponse(
            productUseCase.create(productMapper.toNewDomain(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar producto")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Datos del producto a actualizar",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductRequest.class))
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))
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
            description = "Producto no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productMapper.toResponse(
            productUseCase.update(id, productMapper.toDomain(request))
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))
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
                        value = "{\"timestamp\":\"2026-02-18T18:33:10.932Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:33:16.817Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(
                    name = "NotFound",
                    value = "{\"timestamp\":\"2026-02-18T18:34:17.229Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Producto no encontrado\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                )
            )
        )
    })
    public ResponseEntity<ProductResponse> get(@PathVariable UUID id) {
        return productUseCase.getById(id)
            .map(product -> ResponseEntity.ok(productMapper.toResponse(product)))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "MissingToken",
                        value = "{\"timestamp\":\"2026-02-18T18:33:10.932Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                    ),
                    @ExampleObject(
                        name = "InvalidToken",
                        value = "{\"timestamp\":\"2026-02-18T18:33:16.817Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(
                    name = "NotFound",
                    value = "{\"timestamp\":\"2026-02-18T18:34:17.229Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Producto no encontrado\",\"path\":\"/products/a6da5d57-42d1-40af-a1de-f53b2f8f2d15\"}"
                )
            )
        )
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


