# API Testing Guide

## Overview
This document provides tested examples for all endpoints in the Mantenimiento Productos API.

**Base URL:** `http://localhost:8080`

**Authentication:** JWT Bearer token required for all endpoints except `/users/register` and `/login`

---

## Prerequisites

Start the application with Docker:
```powershell
docker-compose up --build -d
```

Check logs:
```powershell
docker-compose logs -f app
```

---

## PowerShell Examples

### 1. Register User
```powershell
$registerBody = @{
    username = "testuser"
    password = "testpass"
    role = "ROLE_USER"
} | ConvertTo-Json

Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/users/register" `
    -ContentType "application/json" `
    -Body $registerBody
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "role": "ROLE_USER"
}
```

**Status:** `201 Created`

### 2. Login
```powershell
$loginBody = @{
    username = "testuser"
    password = "testpass"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/login" `
    -ContentType "application/json" `
    -Body $loginBody

$token = $response.token
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9..."
}
```

**Possible errors:**

`400 Bad Request`
```json
{
    "timestamp": "2026-02-18T18:25:49.203Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Formato de solicitud inválido",
    "path": "/login"
}
```

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:26:10.174Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Credenciales inválidas",
    "path": "/login"
}
```

### 3. Create Category
```powershell
$headers = @{
    Authorization = "Bearer $token"
}

$categoryBody = @{
    name = "Electronics"
} | ConvertTo-Json

$category = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/categories" `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $categoryBody
```

**Response:**
```json
{
  "id": "a8092c78-726a-4afb-935c-860d796b31e8",
  "name": "Electronics"
}
```

**Status:** `201 Created`

**Possible errors:**

`400 Bad Request`
```json
{
    "timestamp": "2026-02-18T18:31:44.728Z",
    "status": 400,
    "error": "Bad Request",
    "message": "El nombre de la categoría es obligatorio",
    "path": "/categories"
}
```

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:31:10.051Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token ausente",
    "path": "/categories"
}
```

### 4. List Categories
```powershell
$categories = Invoke-RestMethod `
    -Method Get `
    -Uri "http://localhost:8080/categories" `
    -Headers $headers
```

### 5. Get Category by ID
```powershell
$category = Invoke-RestMethod `
    -Method Get `
    -Uri "http://localhost:8080/categories/$categoryId" `
    -Headers $headers
```

**Possible errors:**

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:31:15.174Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token inválido o expirado",
    "path": "/categories/$categoryId"
}
```

`404 Not Found`
```json
{
    "timestamp": "2026-02-18T18:32:33.601Z",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrada",
    "path": "/categories/$categoryId"
}
```

### 6. Update Category
```powershell
$updateCategoryBody = @{
    name = "Updated Electronics"
} | ConvertTo-Json

$updatedCategory = Invoke-RestMethod `
    -Method Put `
    -Uri "http://localhost:8080/categories/$($category.id)" `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $updateCategoryBody
```

**Status:** `200 OK`

**Possible errors:**

`404 Not Found`
```json
{
    "timestamp": "2026-02-19T10:15:22.345Z",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrada",
    "path": "/categories/$categoryId"
}
```

### 7. Delete Category
```powershell
Invoke-RestMethod `
    -Method Delete `
    -Uri "http://localhost:8080/categories/$categoryId" `
    -Headers $headers
```

**Status:** `204 No Content`

**Possible errors:**

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:31:10.051Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token ausente",
    "path": "/categories/$categoryId"
}
```

`404 Not Found`
```json
{
    "timestamp": "2026-02-18T18:32:33.601Z",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría no encontrada",
    "path": "/categories/$categoryId"
}
```

### 8. Create Product
```powershell
$productBody = @{
    name = "Wireless Mouse"
    description = "Ergonomic wireless mouse"
    price = 29.99
    categoryId = $category.id
} | ConvertTo-Json

$product = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/products" `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $productBody
```

**Response:**
```json
{
  "id": "40e3c80b-1eef-454a-bd6d-26e4a5a1b7ef",
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 29.99,
  "categoryId": "a8092c78-726a-4afb-935c-860d796b31e8"
}
```

**Status:** `201 Created`

**Possible errors:**

`400 Bad Request`
```json
{
    "timestamp": "2026-02-18T18:33:48.442Z",
    "status": 400,
    "error": "Bad Request",
    "message": "El precio debe ser mayor que cero",
    "path": "/products"
}
```

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:33:10.932Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token ausente",
    "path": "/products"
}
```

### 9. List Products
```powershell
$products = Invoke-RestMethod `
    -Method Get `
    -Uri "http://localhost:8080/products" `
    -Headers $headers
```

### 10. Get Product by ID
```powershell
$product = Invoke-RestMethod `
    -Method Get `
    -Uri "http://localhost:8080/products/$productId" `
    -Headers $headers
```

**Possible errors:**

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:33:16.817Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token inválido o expirado",
    "path": "/products/$productId"
}
```

`404 Not Found`
```json
{
    "timestamp": "2026-02-18T18:34:17.229Z",
    "status": 404,
    "error": "Not Found",
    "message": "Producto no encontrado",
    "path": "/products/$productId"
}
```

### 11. Update Product
```powershell
$updateProductBody = @{
    name = "Updated Wireless Mouse"
    description = "Premium ergonomic wireless mouse"
    price = 39.99
    categoryId = $category.id
} | ConvertTo-Json

$updatedProduct = Invoke-RestMethod `
    -Method Put `
    -Uri "http://localhost:8080/products/$($product.id)" `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $updateProductBody
```

**Status:** `200 OK`

**Possible errors:**

`404 Not Found`
```json
{
    "timestamp": "2026-02-19T10:20:15.678Z",
    "status": 404,
    "error": "Not Found",
    "message": "Producto no encontrado",
    "path": "/products/$productId"
}
```

### 12. Delete Product
```powershell
Invoke-RestMethod `
    -Method Delete `
    -Uri "http://localhost:8080/products/$productId" `
    -Headers $headers
```

**Status:** `204 No Content`

**Possible errors:**

`401 Unauthorized`
```json
{
    "timestamp": "2026-02-18T18:33:10.932Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Token ausente",
    "path": "/products/$productId"
}
```

`404 Not Found`
```json
{
    "timestamp": "2026-02-18T18:34:17.229Z",
    "status": 404,
    "error": "Not Found",
    "message": "Producto no encontrado",
    "path": "/products/$productId"
}
```

---

## Running the Complete E2E Test

Execute the automated test script:
```powershell
.\test-e2e.ps1
```

This script will:
1. Register a new user
2. Login and obtain JWT token
3. Create a category
4. List all categories
5. Update a category
6. Create a product
7. List all products
8. Get product by ID
9. Update a product
10. Delete the product
11. Delete the category

---

## Troubleshooting

### Application not responding
```powershell
# Check container status
docker-compose ps

# Restart containers
docker-compose restart

# View logs
docker-compose logs app --tail 50
```

### Database connection issues
```powershell
# Check database container
docker-compose logs db --tail 50

# Restart database
docker-compose restart db
```

### JWT token expired
Tokens expire after 15 minutes. Login again to get a fresh token:
```powershell
$loginResponse = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/login" `
    -ContentType "application/json" `
    -Body (@{username="testuser"; password="testpass"} | ConvertTo-Json)

$headers = @{ Authorization = "Bearer $($loginResponse.token)" }
```

---

## Technical Details

- **Framework:** Spring Boot 3 Web MVC (servlet-based)
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL 15 (JDBC)
- **Authentication:** JWT (HS256 algorithm)
- **Password Hashing:** BCrypt
- **Port:** 8080
- **Token Expiration:** 15 minutes

---

## Common Error Response

All errors follow a consistent structure:

```json
{
    "timestamp": "2026-02-18T18:25:43.511Z",
    "status": 400,
    "error": "Bad Request",
    "message": "El nombre de usuario es obligatorio",
    "path": "/login"
}
```

### Validation constraints (unified)

Las validaciones de entrada (`@Valid`) y las de dominio usan las mismas reglas de negocio:

- `CategoryRequest.name`: requerido, máximo 120 caracteres.
- `ProductRequest.name`: requerido, máximo 150 caracteres.
- `ProductRequest.description`: opcional, máximo 500 caracteres.
- `ProductRequest.price`: requerido, mayor que 0.
- `ProductRequest.categoryId`: requerido.
- `UserRegisterRequest.username`: requerido, entre 3 y 50 caracteres.
- `UserRegisterRequest.password`: requerido, entre 6 y 120 caracteres.
- `UserRegisterRequest.role`: opcional, formato `ROLE_<NOMBRE>`.

### Typical error cases

- `400 Bad Request`: validation failures or malformed JSON
- `401 Unauthorized`: missing/invalid token or invalid credentials
- `404 Not Found`: resource does not exist

---

## API Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/users/register` | No | Register new user |
| POST | `/login` | No | Login and get JWT token |
| GET | `/categories` | Yes | List all categories |
| POST | `/categories` | Yes | Create category |
| GET | `/categories/{id}` | Yes | Get category by ID |
| PUT | `/categories/{id}` | Yes | Update category |
| DELETE | `/categories/{id}` | Yes | Delete category |
| GET | `/products` | Yes | List all products |
| POST | `/products` | Yes | Create product |
| GET | `/products/{id}` | Yes | Get product by ID |
| PUT | `/products/{id}` | Yes | Update product |
| DELETE | `/products/{id}` | Yes | Delete product |
