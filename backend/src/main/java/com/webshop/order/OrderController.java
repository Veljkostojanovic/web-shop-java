package com.webshop.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@RequestParam Long userId){
        OrderDTO orderDTO = orderService.createOrder(userId);
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    @GetMapping("/user")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUserId(
            @ParameterObject  @Valid OrderFilterDto filter,
            @ParameterObject Pageable pageable
    ) {
        Page<OrderDTO> page = orderService.getOrdersByUserId(filter, pageable);
        return ResponseEntity.ok(page);
    }
}