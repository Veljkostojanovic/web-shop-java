package com.webshop.product;

import com.webshop.common.util.TextNormalize;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "Product name cannot be blank")
        @Size(max = 255)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        @Digits(integer = 17, fraction = 2)
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stock,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId
) {
        public ProductCreateRequest{
                name = TextNormalize.normalize(name);
                description = TextNormalize.normalize(description);
        }
}
