package com.webshop.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AddItemRequest {
    @NotNull(message = "ProductId is required")
    @Positive(message = "ProductId must be positive")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}