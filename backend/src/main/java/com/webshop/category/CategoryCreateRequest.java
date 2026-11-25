package com.webshop.category;

import com.webshop.common.util.TextNormalize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = Category.MAX_NAME_LENGTH, message = "Category name must not exceed {max} characters")
        String name
) {
    public CategoryCreateRequest {
        name = TextNormalize.normalize(name);
    }
}
