package com.webshop.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(Long userId);

    OrderResponse getOrder(Long orderId, Long userId);

    Page<OrderResponse> getOrdersForUser(Long userId, OrderFilter filter, Pageable pageable);

    OrderResponse cancelOrder(Long orderId, Long userId);

    Page<OrderAdminResponse> adminSearch(AdminOrderFilter filter, Pageable pageable);

    OrderAdminResponse getOrderForAdmin(Long orderId);

    OrderAdminResponse markOrderPaid(Long orderId);

    OrderAdminResponse shipOrder(Long orderId);

    OrderAdminResponse deliverOrder(Long orderId);

    OrderAdminResponse cancelOrderAsAdmin(Long orderId);
}