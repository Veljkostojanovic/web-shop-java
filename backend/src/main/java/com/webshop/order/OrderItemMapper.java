package com.webshop.order;

import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItemResponse toDTO(OrderItem orderItem) {
        if(orderItem == null) return null;
        BigDecimal totalPrice =
                orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                totalPrice
        );
    }
}
