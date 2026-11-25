package com.webshop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderAdminResponse(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {
}