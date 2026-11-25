package com.webshop.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderFilter(
        @Positive(message = "User ID must be positive")
        Long userId,

        OrderStatus status,

        Instant createdFrom,

        Instant createdTo,

        @DecimalMin(value = "0.0", message = "Min total cannot be negative")
        BigDecimal minTotal,

        @DecimalMin(value = "0.0", message = "Max total cannot be negative")
        BigDecimal maxTotal
) {
}
