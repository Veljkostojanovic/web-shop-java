package com.webshop.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name cannot be empty")
        @Size(max = 200)
        String name
) {}
