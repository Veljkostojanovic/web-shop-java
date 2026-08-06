package com.webshop.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterRequest {
    @Past(message = "Date must be in the past")
    private LocalDateTime startDate;

    @Past(message = "Date must be in the past")
    private LocalDateTime endDate;

    @Min(value = 0, message = "Min price cannot be negative")
    private BigDecimal minPrice;

    @Min(value = 0, message = "Max price cannot be negative")
    private BigDecimal maxPrice;
    private OrderStatus status;
}
