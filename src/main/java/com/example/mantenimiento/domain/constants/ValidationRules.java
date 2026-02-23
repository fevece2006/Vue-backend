package com.example.mantenimiento.domain.constants;

import java.math.BigDecimal;

public final class ValidationRules {
    
    private ValidationRules() {
        throw new AssertionError("No se debe instanciar esta clase");
    }
    
    public static final int MAX_CATEGORY_NAME_LENGTH = 120;
    public static final int MAX_PRODUCT_NAME_LENGTH = 150;
    public static final int MAX_PRODUCT_DESCRIPTION_LENGTH = 500;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 120;
    public static final String USER_ROLE_PATTERN = "^ROLE_[A-Z_]+$";
    public static final String DEFAULT_USER_ROLE = "ROLE_USER";
    public static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
}
