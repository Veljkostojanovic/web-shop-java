package com.webshop.order;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@AuthenticationPrincipal UserPrincipal user) {
        OrderDTO orderDTO = orderService.createOrder(user.getId());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal user)
            {
        return ResponseEntity.ok(orderService.getOrder(orderId, user.getId()));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUserId(
            @AuthenticationPrincipal UserPrincipal user,
            @ParameterObject  @Valid OrderFilterRequest filter,
            @ParameterObject Pageable pageable
    ) {
        Page<OrderDTO> page = orderService.getOrdersByUserId(user.getId(), filter, pageable);
        return ResponseEntity.ok(page);
    }
}