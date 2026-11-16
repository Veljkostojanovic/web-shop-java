package com.webshop.order;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long productId,
    String productName,
    BigDecimal productPrice,
    int quantity,
    BigDecimal totalPrice
){}

