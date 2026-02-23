# Arquitectura Clean (Hexagonal) + Principios SOLID

Este documento describe la arquitectura del proyecto, sus capas, principios de diseño aplicados y por qué cada elemento está donde está.

## Vista General

El proyecto aplica **Clean Architecture con enfoque hexagonal** siguiendo estrictamente los **principios SOLID**:

- La lógica de negocio vive en `domain` y `usecase`, completamente independiente de frameworks.
- La entrada/salida del sistema se modela como **adaptadores** (puertos y adaptadores).
- La infraestructura (base de datos, web) se aísla detrás de interfaces.
- **Dependency Inversion Principle (DIP):** Las capas externas dependen de las internas, nunca al revés.

### Flujo de Dependencias

```
Controllers → Use Cases → Domain Models
     ↓            ↓
   DTOs      Repositories → JPA Entities
                ↓
          Entity Mappers (convierte Entity ↔ Domain)
```

La capa Web (controllers) depende de los casos de uso, **no de repositorios**.
Los repositorios son adaptadores salientes y no se usan directamente desde la web.

---

## Capas y Responsabilidades

### Domain (Núcleo del Negocio)

**Ruta:** `src/main/java/com/example/mantenimiento/domain`

#### Modelos de Dominio

**Ruta:** `domain/model/`

Modelos **inmutables** que representan el negocio puro:

- `domain/model/Category.java`
- `domain/model/Product.java`
- `domain/model/User.java`

**Características:**

- **Inmutabilidad:** Todos los campos son `final`, no hay setters.
- **Patrón Builder:** Construcción mediante `Product.builder()...build()`.
- **Validación de Negocio:** Las reglas se validan en el constructor (ej: precio > 0, nombre requerido).
- **Independencia Total:** No conocen JPA, Spring, ni ningún framework.
- **Métodos de Actualización:** `withUpdatedData()` devuelve una nueva instancia (inmutabilidad).
- **Equals/HashCode:** Basados únicamente en el `id`.

**Ejemplo:** 
```java
Product product = Product.builder()
    .id(uuid)
    .name("Mouse")
    .price(BigDecimal.valueOf(19.99))
    .categoryId(categoryId)
    .build(); // Valida en constructor

// Actualización inmutable
Product updated = product.withUpdatedData(newName, newDesc, newPrice, newCatId);
```

#### Excepciones de Dominio

**Ruta:** `domain/exception/`

Jerarquía de excepciones específicas del dominio:

- `domain/exception/DomainException.java` - **Clase base abstracta** para todas las excepciones de dominio.
- `domain/exception/ResourceNotFoundException.java` - Lanzada cuando un recurso no existe.
- `domain/exception/ValidationException.java` - Lanzada cuando falla una validación de negocio.

**Ventaja:** El dominio no depende de excepciones del framework (ej: `ResponseStatusException` de Spring).

#### Constantes de Dominio

**Ruta:** `domain/constants/`

- `domain/constants/ErrorMessages.java` - Mensajes de error centralizados (DRY).
- `domain/constants/ValidationRules.java` - Reglas de validación (longitudes, valores mínimos).

Las reglas se reutilizan en DTOs (adapter incoming) y en modelos de dominio para mantener **una sola fuente de verdad**.

**Reglas unificadas actuales:**

- `MAX_CATEGORY_NAME_LENGTH = 120`
- `MAX_PRODUCT_NAME_LENGTH = 150`
- `MAX_PRODUCT_DESCRIPTION_LENGTH = 500`
- `MIN_USERNAME_LENGTH = 3`
- `MAX_USERNAME_LENGTH = 50`
- `MIN_PASSWORD_LENGTH = 6`
- `MAX_PASSWORD_LENGTH = 120`
- `USER_ROLE_PATTERN = ^ROLE_[A-Z_]+$`

**Ejemplo:**
```java
public final class ErrorMessages {
    public static final String PRODUCT_NOT_FOUND = "Producto";
    public static final String PRODUCT_NAME_REQUIRED = "El nombre del producto es obligatorio";
    // Constructor privado para evitar instanciación
    private ErrorMessages() { throw new AssertionError(); }
}
```

---

### Usecase (Casos de Uso)

**Ruta:** `src/main/java/com/example/mantenimiento/usecase`

Define **QUÉ hace el sistema** (casos de uso) como interfaces.

#### Interfaces (Contratos)

- `usecase/AuthUseCase.java`
- `usecase/CategoryUseCase.java`
- `usecase/ProductUseCase.java`
- `usecase/UserUseCase.java`

**Importante:** Las interfaces usan **modelos de dominio**, no entidades JPA.

```java
public interface ProductUseCase {
    List<Product> list();  // Devuelve Product, no ProductEntity
    Product create(Product product);
    Product update(UUID id, Product product);
    Optional<Product> getById(UUID id);
    void deleteById(UUID id);
}
```

#### Implementaciones

**Ruta:** `usecase/impl/`

- `usecase/impl/AuthUseCaseImpl.java`
- `usecase/impl/CategoryUseCaseImpl.java`
- `usecase/impl/ProductUseCaseImpl.java`
- `usecase/impl/UserUseCaseImpl.java`

**Responsabilidades:**

1. Orquestar la lógica de negocio.
2. Usar repositorios (adaptadores salientes) para persistencia.
3. **Convertir entre Domain Models y JPA Entities** usando mappers.
4. Lanzar excepciones de dominio (`ResourceNotFoundException`, `ValidationException`).

**Ejemplo:**
```java
@Service
public class ProductUseCaseImpl implements ProductUseCase {
    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public Product create(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);
        ProductEntity saved = productRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }
}
```

#### Excepciones de Caso de Uso

- `usecase/exception/InvalidCredentialsException.java` - Específica para autenticación.

---

### Adapter Incoming (Web - Puerto de Entrada)

**Ruta:** `src/main/java/com/example/mantenimiento/adapter/incoming`

Expone la API HTTP REST usando Spring Web MVC.

#### Controllers

**Ruta:** `adapter/incoming/web/`

- `web/AuthController.java`
- `web/CategoryController.java`
- `web/ProductController.java`
- `web/UserController.java`

**Responsabilidades:**

1. **Validar entrada:** Usa `@Valid` en DTOs.
2. **Convertir DTOs a Domain Models:** Usa mappers incoming.
3. **Llamar Use Cases:** Orquesta el flujo mediante interfaces.
4. **Convertir Domain Models a DTOs de respuesta.**
5. **Retornar ResponseEntity** con códigos HTTP apropiados.

**Ejemplo:**
```java
@PostMapping
public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
    Product product = productMapper.toNewDomain(request);  // DTO → Domain
    Product created = productUseCase.create(product);      // Use Case
    ProductResponse response = productMapper.toResponse(created);  // Domain → DTO
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

#### DTOs (Data Transfer Objects)

**Ruta:** `adapter/incoming/dto/`

Contrato público de la API (Request/Response):

- `dto/AuthRequest.java`, `dto/AuthResponse.java`
- `dto/CategoryRequest.java`, `dto/CategoryResponse.java`
- `dto/ProductRequest.java`, `dto/ProductResponse.java`
- `dto/UserRegisterRequest.java`, `dto/UserResponse.java`
- `dto/ApiErrorResponse.java`

**Validaciones:** Usan anotaciones de Bean Validation (`@NotBlank`, `@Positive`, etc.).

**Estrategia por capas (intencional):**

- **DTO + `@Valid`**: validación temprana del contrato HTTP.
- **Dominio**: validación de invariantes de negocio al construir entidades.

Esto evita crear modelos de dominio inválidos desde canales distintos a HTTP (tests, scripts o integraciones internas).

#### Mappers Incoming (DTO ↔ Domain)

**Ruta:** `adapter/incoming/mapper/`

- `mapper/CategoryMapper.java`
- `mapper/ProductMapper.java`
- `mapper/UserMapper.java`

**Métodos clave:**

- `toDomain(RequestDTO)` → Domain Model (sin ID, para updates).
- `toNewDomain(RequestDTO)` → Domain Model (con UUID generado, para creates).
- `toResponse(DomainModel)` → ResponseDTO.

**SRP:** Solo se encargan del mapeo, nada más.

---

### Adapter Outgoing (Persistencia - Puerto de Salida)

**Ruta:** `src/main/java/com/example/mantenimiento/adapter/outgoing`

Implementa la salida a la base de datos usando **Spring Data JPA**.

#### Repositorios (Spring Data JPA)

**Ruta:** `adapter/outgoing/persistence/`

Interfaces que extienden `JpaRepository<Entity, UUID>`:

- `persistence/CategoryRepository.java`
- `persistence/ProductRepository.java`
- `persistence/UserRepository.java`

**Métodos personalizados:**
```java
Optional<UserEntity> findByUsername(String username);
```

#### Entidades JPA (Representan Tablas)

**Ruta:** `adapter/outgoing/persistence/entity/`

- `entity/CategoryEntity.java`
- `entity/ProductEntity.java`
- `entity/UserEntity.java`

**Características:**

- Anotaciones JPA: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`.
- **Mutables:** Tienen setters porque JPA/Hibernate lo requiere.
- **Lazy Loading:** Relaciones con `@ManyToOne(fetch = FetchType.LAZY)`.
- **No se exponen fuera de la capa de persistencia.**

#### Mappers de Persistencia (Entity ↔ Domain)

**Ruta:** `adapter/outgoing/persistence/mapper/`

**CLAVE para DIP:** Convierten entre JPA Entities (infraestructura) y Domain Models (negocio).

- `mapper/CategoryEntityMapper.java`
- `mapper/ProductEntityMapper.java`
- `mapper/UserEntityMapper.java`

**Métodos:**

- `toDomain(Entity)` → Domain Model.
- `toEntity(DomainModel)` → JPA Entity.

**Ejemplo:**
```java
@Component
public class ProductEntityMapper {
    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return Product.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .price(entity.getPrice())
            .categoryId(entity.getCategoryId())
            .build();
    }

    public ProductEntity toEntity(Product product) {
        if (product == null) return null;
        return new ProductEntity(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategoryId()
        );
    }
}
```

**Beneficio:** Los Use Cases trabajan con **modelos de dominio inmutables**, no con entidades JPA mutables.

---

### Config (Configuración Transversal)

**Ruta:** `src/main/java/com/example/mantenimiento/config`

Configuración del sistema y aspectos transversales:

- `config/SecurityConfig.java` - Configuración de Spring Security.
- `config/JwtUtil.java` - Utilidad para generar/validar tokens JWT.
- `config/JwtAuthFilter.java` - Filtro para autenticación JWT.
- `config/OpenApiConfig.java` - Configuración de Swagger/OpenAPI.
- `config/GlobalExceptionHandler.java` - Manejo centralizado de excepciones.

#### GlobalExceptionHandler

**Responsabilidad:** Convertir excepciones de dominio en respuestas HTTP consistentes.

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    ApiErrorResponse error = new ApiErrorResponse(
        ex.getMessage(),
        HttpStatus.NOT_FOUND.value()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}

@ExceptionHandler(ValidationException.class)
public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex) {
    ApiErrorResponse error = new ApiErrorResponse(
        ex.getMessage(),
        HttpStatus.BAD_REQUEST.value()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
}
```

---

## Principios SOLID Aplicados

Este proyecto implementa estrictamente los **5 principios SOLID**:

### 1. Single Responsibility Principle (SRP)

**Cada clase tiene una única responsabilidad:**

- **Controllers:** Solo exponen la API HTTP y orquestan el flujo.
- **Use Cases:** Solo contienen lógica de negocio/orquestación.
- **Mappers Incoming:** Solo convierten DTOs ↔ Domain Models.
- **Mappers Outgoing:** Solo convierten Entities ↔ Domain Models.
- **Repositorios:** Solo acceden a la base de datos.
- **Domain Models:** Solo contienen estado y validaciones de negocio.

**Evidencia:**

- `ProductMapper` solo mapea, no tiene lógica de negocio.
- `ProductUseCaseImpl` solo orquesta, no conoce HTTP ni SQL.
- `ProductController` solo maneja HTTP, no tiene lógica de negocio.

### 2. Open/Closed Principle (OCP)

**Abierto para extensión, cerrado para modificación:**

- **Inmutabilidad:** Los Domain Models son inmutables (campos `final`). Los cambios se hacen mediante métodos que devuelven nuevas instancias (`withUpdatedData()`).
- **Interfaces:** Los Use Cases se definen como interfaces (`ProductUseCase`). Se pueden crear nuevas implementaciones sin modificar los controllers.

**Evidencia:**

```java
// Extensión: Nueva implementación sin cambiar ProductController
public class ProductUseCaseV2Impl implements ProductUseCase { ... }
```

### 3. Liskov Substitution Principle (LSP)

**Los subtipos deben ser sustituibles por sus tipos base:**

- **Jerarquía de Excepciones:** `ResourceNotFoundException` y `ValidationException` extienden `DomainException`. Cualquier código que maneje `DomainException` puede manejar sus subtipos.
- **Interfaces de Use Case:** Cualquier implementación de `ProductUseCase` es intercambiable.

**Evidencia:**

```java
// Cualquier implementación de ProductUseCase es válida
@Autowired
private ProductUseCase productUseCase;  // Puede ser ProductUseCaseImpl o cualquier otra
```

### 4. Interface Segregation Principle (ISP)

**Los clientes no deben depender de interfaces que no usan:**

- **Interfaces Específicas:** Cada Use Case tiene su propia interfaz (`ProductUseCase`, `CategoryUseCase`, `UserUseCase`).
- **No hay interfaz gigante:** No existe un `GenericUseCase` con todos los métodos.
- **Repositorios Específicos:** Cada repositorio extiende `JpaRepository` solo con los métodos que necesita.

**Evidencia:**

```java
// ProductController solo depende de ProductUseCase, no de CategoryUseCase
public class ProductController {
    private final ProductUseCase productUseCase;  // Solo lo que necesita
}
```

### 5. Dependency Inversion Principle (DIP)

**Las dependencias apuntan hacia abstracciones, no hacia concreciones:**

#### Flujo de Dependencias (todas apuntan hacia el dominio):

```
ProductController → ProductUseCase (interfaz) ← ProductUseCaseImpl
                           ↓
                    Product (domain model)
                           ↑
                ProductEntityMapper
                           ↓
                    ProductEntity (JPA)
```

**Evidencia:**

1. **Controllers dependen de interfaces de Use Case, no de implementaciones:**
   ```java
   private final ProductUseCase productUseCase;  // Interfaz, no ProductUseCaseImpl
   ```

2. **Use Cases trabajan con Domain Models, no con Entities JPA:**
   ```java
   public interface ProductUseCase {
       List<Product> list();  // Product, no ProductEntity
   }
   ```

3. **Mappers convierten entre infraestructura (JPA) y dominio:**
   ```java
   Product domain = productEntityMapper.toDomain(entity);
   ```

4. **Domain Models no conocen JPA, Spring, ni ningún framework:**
   - No hay anotaciones `@Entity`, `@Table`, `@Column` en `Product.java`.
   - Solo lógica de negocio pura.

**Beneficio:** El dominio es independiente de la infraestructura. Se puede cambiar de JPA a MongoDB sin tocar los Use Cases o Domain Models.

---

## Patrones de Diseño Aplicados

### Builder Pattern

Usado en todos los Domain Models para construcción inmutable:

```java
Product product = Product.builder()
    .name("Mouse")
    .description("Inalámbrico")
    .price(BigDecimal.valueOf(19.99))
    .categoryId(categoryId)
    .build();
```

### Strategy Pattern

Las interfaces de Use Case permiten intercambiar implementaciones (estrategias):

```java
@Service
public class ProductUseCaseImpl implements ProductUseCase { ... }

// Se puede crear otra estrategia sin cambiar los controllers
@Service
public class CachedProductUseCaseImpl implements ProductUseCase { ... }
```

### Dependency Injection

Spring inyecta dependencias mediante constructores:

```java
public ProductController(ProductUseCase productUseCase, ProductMapper productMapper) {
    this.productUseCase = productUseCase;
    this.productMapper = productMapper;
}
```

---

## Beneficios de Esta Arquitectura

### Testabilidad

- **Unit Tests:** Use Cases se testean sin Spring, sin base de datos.
- **Mocks:** Los repositorios y mappers se mockean fácilmente.
- **Inmutabilidad:** Los Domain Models inmutables son predecibles y seguros en tests.

**Ejemplo:**
```java
@Mock
private ProductRepository productRepository;
@Mock
private ProductEntityMapper productEntityMapper;

@Test
void create_savesProduct() {
    Product product = Product.builder()...build();
    when(productEntityMapper.toEntity(product)).thenReturn(entity);
    when(productRepository.save(entity)).thenReturn(savedEntity);
    when(productEntityMapper.toDomain(savedEntity)).thenReturn(product);
    
    Product result = productUseCase.create(product);
    assertEquals(product, result);
}
```

### Mantenibilidad

- **SRP:** Cambios en DTOs no afectan Use Cases.
- **DIP:** Cambios en persistencia (JPA → MongoDB) solo afectan mappers y repositorios.
- **Inmutabilidad:** No hay efectos secundarios inesperados.

### Escalabilidad

- **Nuevos Features:** Se agregan nuevos Use Cases sin modificar existentes.
- **Nuevos Adaptadores:** Se pueden agregar adaptadores (GraphQL, gRPC) sin tocar Use Cases.

---

## Flujo Típico de una Petición

### Ejemplo: Crear un Producto

1. **HTTP Request** → `POST /products` con `ProductRequest` JSON.

2. **JwtAuthFilter** valida el token JWT y establece el contexto de seguridad.

3. **ProductController** recibe el request:
   ```java
   @PostMapping
   public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request)
   ```

4. **Validación DTO:** Spring valida `@Valid` en `ProductRequest` (ej: `@NotBlank`, `@Positive`).

5. **Mapper Incoming:** Convierte `ProductRequest` → `Product` (domain model):
   ```java
   Product product = productMapper.toNewDomain(request);
   ```

6. **Use Case:** Controller llama a `ProductUseCase`:
   ```java
   Product created = productUseCase.create(product);
   ```

7. **Mapper Outgoing:** Use Case convierte `Product` → `ProductEntity`:
   ```java
   ProductEntity entity = productEntityMapper.toEntity(product);
   ```

8. **Repositorio JPA:** Guarda la entidad en PostgreSQL:
   ```java
   ProductEntity saved = productRepository.save(entity);
   ```

9. **Mapper Outgoing:** Use Case convierte `ProductEntity` → `Product`:
   ```java
   return productEntityMapper.toDomain(saved);
   ```

10. **Mapper Incoming:** Controller convierte `Product` → `ProductResponse`:
    ```java
    ProductResponse response = productMapper.toResponse(created);
    ```

11. **HTTP Response:** Retorna `201 Created` con `ProductResponse` JSON.

### Gestión de Errores

Si falla alguna validación en el dominio:

```java
// En Product.java constructor
if (price.compareTo(ValidationRules.MIN_PRICE) <= 0) {
    throw new ValidationException("El precio debe ser mayor a 0");
}
```

El `GlobalExceptionHandler` captura y convierte:

```java
@ExceptionHandler(ValidationException.class)
public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiErrorResponse(ex.getMessage(), 400));
}
```

Cliente recibe `400 Bad Request` con mensaje claro.

---

## Ubicación de Archivos Clave

### Dominio (Núcleo)
- **Modelos de Dominio:** `domain/model/` → `Product.java`, `Category.java`, `User.java`
- **Excepciones:** `domain/exception/` → `DomainException.java`, `ResourceNotFoundException.java`, `ValidationException.java`
- **Constantes:** `domain/constants/` → `ErrorMessages.java`, `ValidationRules.java`

### Casos de Uso
- **Interfaces:** `usecase/` → `ProductUseCase.java`, etc.
- **Implementaciones:** `usecase/impl/` → `ProductUseCaseImpl.java`, etc.

### Adaptadores Entrantes (Web)
- **Controllers:** `adapter/incoming/web/` → `ProductController.java`, etc.
- **DTOs:** `adapter/incoming/dto/` → `ProductRequest.java`, `ProductResponse.java`, etc.
- **Mappers:** `adapter/incoming/mapper/` → `ProductMapper.java` (DTO ↔ Domain)

### Adaptadores Salientes (Persistencia)
- **Repositorios:** `adapter/outgoing/persistence/` → `ProductRepository.java`, etc.
- **Entidades JPA:** `adapter/outgoing/persistence/entity/` → `ProductEntity.java`, etc.
- **Mappers:** `adapter/outgoing/persistence/mapper/` → `ProductEntityMapper.java` (Entity ↔ Domain)

### Configuración
- **Security, JWT, Swagger, Excepciones:** `config/`

---

## Resumen de Clean Architecture + SOLID

| Principio | Aplicación en el Proyecto |
|-----------|---------------------------|
| **Clean Architecture** | Capas bien definidas (Domain → Use Case → Adapters). Dependencias apuntan hacia adentro. |
| **Hexagonal (Ports & Adapters)** | Use Cases = Puertos. Controllers/Repositories = Adaptadores. |
| **SRP** | Cada clase tiene una sola responsabilidad (mapper, controller, use case, etc.). |
| **OCP** | Domain Models inmutables. Use Cases extensibles mediante interfaces. |
| **LSP** | Jerarquía de excepciones intercambiables. Implementaciones de Use Case sustituibles. |
| **ISP** | Interfaces específicas por entidad. No interfaces gigantes. |
| **DIP** | Controllers dependen de interfaces. Use Cases usan Domain Models, no JPA Entities. |
| **Inmutabilidad** | Domain Models con campos `final`, Builder pattern, métodos `withUpdatedX()`. |
| **Validación de Negocio** | Reglas validadas en constructores de Domain Models, no solo en DTOs. |
| **Excepciones de Dominio** | Independientes de frameworks (no `ResponseStatusException`). |

---

## Comparación: Antes vs. Después

### ❌ Antes (Acoplado)

```java
// Controller acoplado a Entity JPA
@PostMapping
public ResponseEntity<ProductEntity> create(@RequestBody ProductEntity product) {
    ProductEntity saved = productRepository.save(product);  // Controller usa Repository
    return ResponseEntity.ok(saved);  // Expone Entity JPA al cliente
}
```

**Problemas:**
- Controller depende de Repository (violación de capas).
- Se expone `ProductEntity` (JPA) al cliente.
- No hay validación de negocio.
- Difícil de testear sin base de datos.

### ✅ Después (Desacoplado)

```java
// Controller depende de Use Case (interfaz)
@PostMapping
public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    Product product = productMapper.toNewDomain(request);     // DTO → Domain
    Product created = productUseCase.create(product);         // Use Case (interfaz)
    ProductResponse response = productMapper.toResponse(created);  // Domain → DTO
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**Beneficios:**
- Controller solo conoce Use Case (interfaz).
- DTOs separan contrato público de modelos internos.
- Validación en Domain Model (constructor).
- Testeable con mocks, sin base de datos.

---

## Conclusión

Este proyecto implementa **Clean Architecture con principios SOLID** de manera estricta:

✅ **Separación de Capas:** Domain → Use Cases → Adapters.  
✅ **Inversión de Dependencias:** Todas apuntan hacia el dominio.  
✅ **Inmutabilidad:** Domain Models inmutables con Builder pattern.  
✅ **Validación de Negocio:** En el dominio, no solo en DTOs.  
✅ **Excepciones de Dominio:** Independientes de frameworks.  
✅ **Testabilidad:** Unit tests sin infraestructura.  
✅ **Mantenibilidad:** Cambios localizados, bajo acoplamiento.  

**El resultado es un código robusto, escalable y fácil de mantener.**
