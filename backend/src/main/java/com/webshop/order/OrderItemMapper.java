package com.webshop.order;

import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItemDTO toDTO(OrderItem orderItem) {
        if(orderItem == null) return null;
        BigDecimal totalPrice =
                orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return new OrderItemDTO(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                totalPrice
        );
    }
}
