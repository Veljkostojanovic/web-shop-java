package com.webshop.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.webshop.order.OrderEntity;
import com.webshop.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createPayment(@PathVariable Long orderId) {
        try {
            OrderEntity order = orderService.getById(orderId);
            PaymentIntent intent = paymentService.createPaymentIntent(order);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "paymentIntentId", intent.getId()
            ));

        } catch (StripeException e) {
            return ResponseEntity.status(502).body(Map.of(
                    "error", "Stripe error: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal error: " + e.getMessage()
            ));
        }
    }
}