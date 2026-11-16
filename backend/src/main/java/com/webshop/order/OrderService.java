package com.webshop.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(Long userId);
    OrderResponse getOrderById(Long orderId);
    OrderResponse getOrder(Long orderId, Long userId);
    Page<OrderResponse> getOrdersByUserId(Long userId, OrderFilterRequest filterRequest, Pageable pageable);
    Page<OrderResponse> getOrders(OrderFilter filter, Pageable pageable);
}
