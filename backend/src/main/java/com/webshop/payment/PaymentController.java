package com.webshop.payment;

import com.stripe.model.PaymentIntent;
import com.webshop.order.OrderEntity;
import com.webshop.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/create/{orderId}")
    public Map<String, String> createPayment(@PathVariable Long orderId) throws Exception {

        OrderEntity order = orderService.getById(orderId);

        PaymentIntent intent = paymentService.createPaymentIntent(order);

        return Map.of(
                "clientSecret", intent.getClientSecret()
        );
    }
}