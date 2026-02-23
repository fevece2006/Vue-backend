package com.example.mantenimiento.domain.constants;

public final class ErrorMessages {
    
    private ErrorMessages() {
        throw new AssertionError("No se debe instanciar esta clase");
    }
    
    // Product errors
    public static final String PRODUCT_NOT_FOUND = "Producto";
    public static final String PRODUCT_NAME_REQUIRED = "El nombre del producto es obligatorio";
    public static final String PRODUCT_NAME_MAX_LENGTH = "El nombre del producto no puede superar 150 caracteres";
    public static final String PRODUCT_DESCRIPTION_MAX_LENGTH = "La descripción no puede superar 500 caracteres";
    public static final String PRODUCT_PRICE_POSITIVE = "El precio debe ser mayor que cero";
    public static final String PRODUCT_CATEGORY_REQUIRED = "La categoría es obligatoria";
    
    // Category errors
    public static final String CATEGORY_NOT_FOUND = "Categoría";
    public static final String CATEGORY_NAME_REQUIRED = "El nombre de la categoría es obligatorio";
    public static final String CATEGORY_NAME_MAX_LENGTH = "El nombre de la categoría no puede superar 120 caracteres";
    
    // User errors
    public static final String USER_NOT_FOUND = "Usuario";
    public static final String USER_USERNAME_REQUIRED = "El nombre de usuario es obligatorio";
    public static final String USER_USERNAME_LENGTH = "El nombre de usuario debe tener entre 3 y 50 caracteres";
    public static final String USER_PASSWORD_REQUIRED = "La contraseña es obligatoria";
    public static final String USER_PASSWORD_LENGTH = "La contraseña debe tener entre 6 y 120 caracteres";
    public static final String USER_ROLE_INVALID_FORMAT = "El rol debe cumplir el formato ROLE_<NOMBRE>";
    
    // Auth errors
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
}
