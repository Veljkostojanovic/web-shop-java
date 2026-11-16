package com.webshop.order;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal UserPrincipal user) {
        OrderResponse orderResponse = orderService.createOrder(user.getId());
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable @Positive Long orderId,
            @AuthenticationPrincipal UserPrincipal user)
            {
        return ResponseEntity.ok(orderService.getOrder(orderId, user.getId()));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
            @AuthenticationPrincipal UserPrincipal user,
            @ParameterObject @Valid OrderFilterRequest filter,
            @ParameterObject Pageable pageable
    ) {
        Page<OrderResponse> page = orderService.getOrdersByUserId(user.getId(), filter, pageable);
        return ResponseEntity.ok(page);
    }
}