package com.webshop.order;

import java.math.BigDecimal;

public class OrderMapper {
    public static OrderResponse mapToDTO(OrderEntity order){
        var itemsDTO = order.getOrderItems().stream()
                .map(OrderMapper::mapItemToDTO)
                .toList();
        return new OrderResponse(order.getId(), order.getOrderDate(), order.getTotalAmount(), itemsDTO);
    }

    public static OrderItemResponse mapItemToDTO(OrderItem item) {
        var itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getPrice(),
                item.getQuantity(),
                itemTotal
        );
    }
}
