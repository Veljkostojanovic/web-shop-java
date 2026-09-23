package com.webshop.product;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record ProductFilterDto(
    String name,
    Long categoryId,

    @DecimalMin(value = "0.0", message = "Min price cannot be negative")
    BigDecimal minPrice,

    @DecimalMin(value = "0.0", message = "Max price cannot be negative")
    BigDecimal maxPrice,
    Integer stock
) {}
