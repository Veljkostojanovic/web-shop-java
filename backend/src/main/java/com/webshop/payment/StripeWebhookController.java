package com.webshop.payment;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.webshop.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws Exception {

        String payload = request.getReader().lines().collect(Collectors.joining());
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if ("payment_intent.succeeded".equals(event.getType())) {

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

            if (stripeObject instanceof PaymentIntent intent) {

                String orderId = intent.getMetadata().get("orderId");
                orderService.markAsPaid(Long.parseLong(orderId));
            }
        }
        return ResponseEntity.ok().build();
    }
}