package com.webshop.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderAdminResponse>> searchOrders(@ParameterObject @Valid AdminOrderFilter filter,
                                                                 @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.adminSearch(filter, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderAdminResponse> getOrder(@PathVariable @Positive Long orderId) {
        return ResponseEntity.ok(orderService.getOrderForAdmin(orderId));
    }

    @PostMapping("/{orderId}/mark-paid")
    public ResponseEntity<OrderAdminResponse> markPaid(@PathVariable @Positive Long orderId) {
        return ResponseEntity.ok(orderService.markOrderPaid(orderId));
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderAdminResponse> ship(@PathVariable @Positive Long orderId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<OrderAdminResponse> deliver(@PathVariable @Positive Long orderId) {
        return ResponseEntity.ok(orderService.deliverOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderAdminResponse> cancel(@PathVariable @Positive Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrderAsAdmin(orderId));
    }
}
