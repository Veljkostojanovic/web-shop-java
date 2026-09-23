package com.webshop.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank(message = "Product name cannot be blank")
        String name,

        String description,

        @NotNull @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull @PositiveOrZero(message = "Stock cannot be negative")
        int stock,

        @NotNull Long categoryId
) {}
