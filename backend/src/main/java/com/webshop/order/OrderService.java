package com.webshop.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    OrderDTO getOrderById(Long orderId);
    Page<OrderDTO> getOrdersByUserId(OrderFilterDto filter, Pageable pageable);
}
