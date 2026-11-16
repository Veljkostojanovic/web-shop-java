package com.webshop.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long orderId,
    LocalDateTime orderDate,
    BigDecimal totalAmount,
    List<OrderItemResponse> items
){}

