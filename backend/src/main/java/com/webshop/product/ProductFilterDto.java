package com.webshop.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductFilterDto(
        @Size(max = 255, message = "Product name cannot exceed 255 characters")
    String name,

    @Positive(message = "Category ID must be positive")
    Long categoryId,

    @DecimalMin(value = "0.0", message = "Min price cannot be negative")
    BigDecimal minPrice,

    @DecimalMin(value = "0.0", message = "Max price cannot be negative")
    BigDecimal maxPrice,

    @PositiveOrZero(message = "Min stock cannot be negative")
    Integer minStock
) {}
