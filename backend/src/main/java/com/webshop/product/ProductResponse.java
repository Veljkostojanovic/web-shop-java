package com.webshop.product;

import com.webshop.category.CategoryResponse;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        CategoryResponse category
) {
}
