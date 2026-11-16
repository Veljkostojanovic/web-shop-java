package com.webshop.cart;

import java.math.BigDecimal;

public record CartItemResponse(
    Long productId,
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal totalPrice
){}

