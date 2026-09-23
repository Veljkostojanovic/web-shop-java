package com.webshop.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryResponse(
    Long id,
    @NotBlank(message = "Category name cannot be empty")
    String name
){}
