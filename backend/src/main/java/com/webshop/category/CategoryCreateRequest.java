package com.webshop.category;

import com.webshop.common.util.TextNormalize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name
) {
        public CategoryCreateRequest {
                name = TextNormalize.normalize(name);
        }
}
