package com.webshop.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    OrderDTO getOrderById(Long orderId);
    OrderDTO getOrder(Long orderId, Long userId);
    Page<OrderDTO> getOrdersByUserId(Long userId, OrderFilterRequest filterRequest, Pageable pageable);
    Page<OrderDTO> getOrders(OrderFilter filter, Pageable pageable);
}
