package com.webshop.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(
        @NotNull(message = "Delta is required")
        @Min(value = -1_000_000, message = "Delta must be at least {value}")
        @Max(value = 1_000_000, message = "Delta must be at most {value}")
        Integer delta
) {
}