package com.webshop.order;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByUserId(Long userId);
    OrderEntity getById(Long orderId);
    void markAsPaid(Long orderId);
}
