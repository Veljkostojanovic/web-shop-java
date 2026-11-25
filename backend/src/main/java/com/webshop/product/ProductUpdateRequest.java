package com.webshop.product;

import com.webshop.common.util.TextNormalize;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank(message = "Product name cannot be blank")
        @Size(max = Product.MAX_NAME_LENGTH, message = "Product name cannot exceed {max} characters")
        String name,

        @Size(max = Product.MAX_DESCRIPTION_LENGTH, message = "Description cannot exceed {max} characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        @Digits(integer = Product.PRICE_PRECISION - Product.PRICE_SCALE, fraction = Product.PRICE_SCALE,
                message = "Price can have at most {integer} digits and {fraction} decimals")
        BigDecimal price,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId
) {
    public ProductUpdateRequest {
        name = TextNormalize.normalize(name);
    }
}
