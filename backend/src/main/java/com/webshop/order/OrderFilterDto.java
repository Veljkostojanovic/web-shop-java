package com.webshop.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterDto {

    @NotNull(message = "User ID is required")
    private Long userId;

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