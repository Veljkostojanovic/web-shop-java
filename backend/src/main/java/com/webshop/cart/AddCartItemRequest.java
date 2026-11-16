package com.webshop.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(
    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    Long productId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 300, message = "Quantity cannot exceed 300")
    Integer quantity
){}
