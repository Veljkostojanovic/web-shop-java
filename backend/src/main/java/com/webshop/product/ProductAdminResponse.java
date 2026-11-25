package com.webshop.product;

import com.webshop.category.CategoryResponse;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductAdminResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        CategoryResponse category,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
