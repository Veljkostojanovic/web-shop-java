package com.webshop.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderFilterRequest(
    @Past(message = "Date must be in the past")
    LocalDateTime startDate,

    @Past(message = "Date must be in the past")
    LocalDateTime endDate,

    @Min(value = 0, message = "Min price cannot be negative")
    BigDecimal minPrice,

    @Min(value = 0, message = "Max price cannot be negative")
    BigDecimal maxPrice,

    OrderStatus status
){}
