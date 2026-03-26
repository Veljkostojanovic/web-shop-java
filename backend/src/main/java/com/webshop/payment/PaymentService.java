package com.webshop.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.webshop.order.OrderEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    public PaymentIntent createPaymentIntent(OrderEntity order) throws StripeException {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue());
        params.put("currency", "usd");

        params.put("metadata", Map.of(
                "orderId", order.getId().toString()
        ));

        return PaymentIntent.create(params);
    }
}