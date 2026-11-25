package com.webshop.order;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                toItemResponses(order)
        );
    }

    public static OrderAdminResponse toAdminResponse(OrderEntity order) {
        return new OrderAdminResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                toItemResponses(order)
        );
    }

    private static List<OrderItemResponse> toItemResponses(OrderEntity order) {
        return order.getOrderItems().stream()
                .map(OrderMapper::toItemResponse)
                .toList();
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.lineTotal()
        );
    }
}
