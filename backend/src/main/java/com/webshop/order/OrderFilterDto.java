package com.webshop.order;

import jakarta.validation.constraints.NotNull;
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

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private OrderStatus status;
}