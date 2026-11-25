package com.webshop.order;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserPrincipal user) {
        OrderResponse created = orderService.createOrder(user.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserPrincipal user,
                                                           @ParameterObject @Valid OrderFilter filter,
                                                           @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersForUser(user.getId(), filter, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(@PathVariable @Positive Long orderId,
                                                    @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderService.getOrder(orderId, user.getId()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelMyOrder(@PathVariable @Positive Long orderId,
                                                       @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, user.getId()));
    }
}