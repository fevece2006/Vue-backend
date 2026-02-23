# Mantenimiento de Productos - Backend

✅ **Status:** Production-ready | All endpoints validated  
🏗️ **Architecture:** Clean Architecture + SOLID Principles  
🔒 **Immutability:** Domain-Driven Design with immutable models

## Tech Stack

- **Java:** 17
- **Framework:** Spring Boot 3 (Web MVC)
- **Architecture:** Clean Architecture (Hexagonal) + SOLID
- **Domain Models:** Immutable objects with Builder pattern
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL 15
- **Authentication:** JWT (JSON Web Tokens) with BCrypt
- **Build:** Gradle 8.14
- **Deployment:** Docker Compose

### Architecture Highlights

This project follows **Clean Architecture** with strict **SOLID principles**:

- ✅ **Separation of Concerns:** Domain layer independent of frameworks
- ✅ **Dependency Inversion:** Use Cases depend on Domain Models, not JPA Entities
- ✅ **Immutability:** Domain Models are immutable with final fields and Builder pattern
- ✅ **Domain Exceptions:** Custom exceptions (`ResourceNotFoundException`, `ValidationException`)
- ✅ **Entity Mappers:** Explicit conversion between JPA Entities and Domain Models
- ✅ **Single Responsibility:** Each class has one responsibility (Controllers, Use Cases, Mappers)

**See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed documentation on Clean Architecture and SOLID principles.**

### Persistence Architecture

- This project uses **Spring Data JPA / Hibernate** for persistence.
- Lazy loading is enabled via `enable_lazy_load_no_trans: true` in Hibernate configuration.
- All repositories extend `JpaRepository<Entity, UUID>`.

#### Repository Interfaces (Spring Data JPA)
- `src/main/java/com/example/mantenimiento/adapter/outgoing/persistence/UserRepository.java`
- `src/main/java/com/example/mantenimiento/adapter/outgoing/persistence/CategoryRepository.java`
- `src/main/java/com/example/mantenimiento/adapter/outgoing/persistence/ProductRepository.java`

---

## Quick Start

### 1. Start with Docker
```powershell
docker-compose up --build -d
```

### 2. Test the API
```powershell
# Register user
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/users/register' -ContentType 'application/json' -Body '{"username":"testuser","password":"testpass","role":"ROLE_USER"}'

# Login
$login = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/login' -ContentType 'application/json' -Body '{"username":"testuser","password":"testpass"}'
$headers = @{ Authorization = "Bearer $($login.token)" }

# Create category
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/categories' -Headers $headers -ContentType 'application/json' -Body '{"name":"Electronics"}'
```

---

## API Endpoints

### Authentication (Public)
- `POST /login` - Login and receive JWT token
- `POST /users/register` - Register new user

### Categories (Requires JWT)
- `GET /categories` - List all categories
- `POST /categories` - Create category
- `GET /categories/{id}` - Get category by ID
- `PUT /categories/{id}` - Update category
- `DELETE /categories/{id}` - Delete category

### Products (Requires JWT)  
- `GET /products` - List all products
- `POST /products` - Create product
- `GET /products/{id}` - Get product by ID
- `PUT /products/{id}` - Update product
- `DELETE /products/{id}` - Delete product

### HTTP Status Codes

| Operation | Success | Error |
|---|---:|---|
| Login | `200 OK` | `401 Unauthorized` |
| Register | `201 Created` | `400 Bad Request` |
| List | `200 OK` | `401 Unauthorized` |
| Create | `201 Created` | `400 Bad Request`, `401 Unauthorized` |
| Get | `200 OK` | `401 Unauthorized`, `404 Not Found` |
| Update | `200 OK` | `401 Unauthorized`, `404 Not Found` |
| Delete | `204 No Content` | `401 Unauthorized`, `404 Not Found` |

---

## API Documentation (Swagger / OpenAPI)

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### JWT Authorization in Swagger
1. Execute `POST /login` to get a token.
2. Click **Authorize** in Swagger UI.
3. Enter: `Bearer <your-token>`
4. Execute secured endpoints.

---

## Configuration

### Environment Variables (docker-compose.yml)
- `SPRING_DATASOURCE_URL` - Database JDBC URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT signing (minimum 32 bytes)

### Local Development
Override in [src/main/resources/application.yml](src/main/resources/application.yml)

---

## Build & Run Locally

### Using Gradle
```powershell
# Clean build
.\gradlew clean build

# Run application
.\gradlew bootRun
```

### With Docker
```powershell
# Start services
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

---

## Architecture

### Clean Architecture (Hexagonal) + SOLID Principles

This project implements **Clean Architecture** with strict adherence to **SOLID principles**.

**📖 See [ARCHITECTURE.md](ARCHITECTURE.md) for complete documentation with examples and detailed explanations.**

#### Layer Structure

```
src/main/java/com/example/mantenimiento/
├── domain/                         # 🎯 Core Business Logic (Framework-Independent)
│   ├── model/                      # Immutable Domain Models (Builder pattern)
│   │   ├── Product.java            # Business entity with validation
│   │   ├── Category.java
│   │   └── User.java
│   ├── exception/                  # Domain-specific exceptions
│   │   ├── DomainException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ValidationException.java
│   └── constants/                  # Business rules and messages
│       ├── ErrorMessages.java
│       └── ValidationRules.java
│
├── usecase/                        # 🎯 Application Business Rules
│   ├── ProductUseCase.java         # Interfaces (Dependency Inversion)
│   ├── CategoryUseCase.java
│   ├── UserUseCase.java
│   ├── AuthUseCase.java
│   └── impl/                       # Implementations
│       ├── ProductUseCaseImpl.java # Orchestrates domain logic
│       ├── CategoryUseCaseImpl.java
│       ├── UserUseCaseImpl.java
│       └── AuthUseCaseImpl.java
│
├── adapter/                        # 🔌 External Interfaces (Ports & Adapters)
│   ├── incoming/                   # Inbound Adapters (API)
│   │   ├── web/                    # REST Controllers
│   │   │   ├── ProductController.java
│   │   │   ├── CategoryController.java
│   │   │   ├── UserController.java
│   │   │   └── AuthController.java
│   │   ├── dto/                    # Data Transfer Objects (API contract)
│   │   │   ├── ProductRequest.java
│   │   │   ├── ProductResponse.java
│   │   │   └── ...
│   │   └── mapper/                 # DTO ↔ Domain Model conversion
│   │       ├── ProductMapper.java
│   │       ├── CategoryMapper.java
│   │       └── UserMapper.java
│   │
│   └── outgoing/                   # Outbound Adapters (Infrastructure)
│       └── persistence/
│           ├── ProductRepository.java      # Spring Data JPA interfaces
│           ├── CategoryRepository.java
│           ├── UserRepository.java
│           ├── entity/                      # JPA Entities (mutable)
│           │   ├── ProductEntity.java
│           │   ├── CategoryEntity.java
│           │   └── UserEntity.java
│           └── mapper/                      # Entity ↔ Domain Model conversion
│               ├── ProductEntityMapper.java
│               ├── CategoryEntityMapper.java
│               └── UserEntityMapper.java
│
└── config/                         # ⚙️ Cross-cutting Concerns
    ├── SecurityConfig.java
    ├── JwtUtil.java
    ├── JwtAuthFilter.java
    ├── OpenApiConfig.java
    └── GlobalExceptionHandler.java  # Maps domain exceptions to HTTP
```

#### SOLID Principles Applied

| Principle | Implementation |
|-----------|----------------|
| **Single Responsibility (SRP)** | Controllers handle HTTP, Use Cases handle business logic, Mappers handle conversion |
| **Open/Closed (OCP)** | Immutable Domain Models, extensible via interfaces |
| **Liskov Substitution (LSP)** | Domain exception hierarchy, Use Case implementations substitutable |
| **Interface Segregation (ISP)** | Specific interfaces per entity (`ProductUseCase`, not `GenericUseCase`) |
| **Dependency Inversion (DIP)** | Controllers depend on `UseCase` interfaces, Use Cases work with Domain Models not JPA Entities |

#### Key Design Decisions

**1. Immutable Domain Models**
```java
// Domain Models are immutable with Builder pattern
Product product = Product.builder()
    .name("Mouse")
    .price(BigDecimal.valueOf(19.99))
    .categoryId(categoryId)
    .build();  // Validates business rules in constructor
```

**2. Separation: Domain Models vs. JPA Entities**
- **Domain Models** (`Product`) - Immutable, business logic, framework-independent
- **JPA Entities** (`ProductEntity`) - Mutable, persistence details, hidden in adapter layer
- **Entity Mappers** - Convert between the two layers

**3. Domain Exceptions**
```java
// Use Cases throw domain exceptions, not framework exceptions
throw new ResourceNotFoundException("Producto", productId);

// GlobalExceptionHandler maps to HTTP responses
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiErrorResponse> handle(ResourceNotFoundException ex) {
    return ResponseEntity.status(404).body(new ApiErrorResponse(ex.getMessage(), 404));
}
```

### Request Flow Example

```
Client Request (JSON)
    ↓
[ProductController]  - Validates DTO, converts to Domain Model
    ↓ calls
[ProductUseCase] interface  - Business logic orchestration
    ↓ implements
[ProductUseCaseImpl]  - Uses ProductEntityMapper to convert Domain ↔ Entity
    ↓ calls
[ProductRepository]  - Spring Data JPA persistence
    ↓
Database (PostgreSQL)
```

### Security Flow

1. Client credentials → `/login` endpoint
2. Server validates and generates JWT token
3. Client sends token in `Authorization: Bearer <token>` header
4. `JwtAuthFilter` validates token on each request
5. Valid requests proceed to controllers with authenticated user context

---

## Database

### Tables
- `users` - User accounts with BCrypt hashed passwords
- `categories` - Product categories
- `products` - Products with foreign key to categories

### Initialization
Schema created automatically on startup from [db/init.sql](db/init.sql).

**Default admin user:**
- Username: `admin`
- Password: `password`
- Role: `ROLE_ADMIN`

---

## Testing

### Run Unit Tests
```powershell
.\gradlew test
```

### Domain Validation Tests

Además de los tests de casos de uso, el proyecto incluye tests unitarios de invariantes en modelos de dominio:

- `src/test/java/com/example/mantenimiento/domain/model/UserTest.java`
- `src/test/java/com/example/mantenimiento/domain/model/ProductTest.java`
- `src/test/java/com/example/mantenimiento/domain/model/CategoryTest.java`

Estos tests validan reglas centralizadas en `ValidationRules` y mensajes en `ErrorMessages`.

### Validation Rules (Single Source of Truth)

Las reglas de negocio se definen en `domain/constants/ValidationRules.java` y se reutilizan en:

- DTOs de entrada (Bean Validation con `@Valid`)
- Modelos de dominio (invariantes en constructor/builder)

Reglas principales actualmente unificadas:

- Category: nombre máximo 120
- Product: nombre máximo 150, descripción máxima 500, precio > 0
- User: username entre 3 y 50, password entre 6 y 120, rol con patrón `^ROLE_[A-Z_]+$`

### Manual API Testing
```powershell
# Login
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/login `
  -ContentType 'application/json' `
  -Body '{"username":"admin","password":"password"}'
$token = $login.token

# Create category
Invoke-RestMethod -Method Post -Uri http://localhost:8080/categories `
  -Headers @{ Authorization = "Bearer $token" } `
  -ContentType 'application/json' `
  -Body '{"name":"Electronics"}'

# List products
Invoke-RestMethod -Method Get -Uri http://localhost:8080/products `
  -Headers @{ Authorization = "Bearer $token" }
```

---

## Troubleshooting

### Application won't start
```powershell
# Check Docker containers
docker-compose ps

# View logs
docker-compose logs app

# Rebuild
docker-compose down -v
docker-compose up --build
```

### Compilation errors
```powershell
# Clean and rebuild
.\gradlew clean build
```

### JWT token invalid
- Tokens expire after 15 minutes
- Login again to get a fresh token

### Database connection failed
```powershell
docker-compose ps db
docker-compose logs db
```
