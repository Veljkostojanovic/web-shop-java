package com.webshop.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = CartItem.MIN_QUANTITY, message = "Quantity must be at least {value}")
        @Max(value = CartItem.MAX_QUANTITY, message = "Quantity cannot exceed {value}")
        Integer quantity
) {
}