package com.webshop.category;

import com.webshop.common.util.TextNormalize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
        @NotBlank(message = "Category name cannot be empty")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name
) {
        public CategoryUpdateRequest{
                name = TextNormalize.normalize(name);
        }
}
