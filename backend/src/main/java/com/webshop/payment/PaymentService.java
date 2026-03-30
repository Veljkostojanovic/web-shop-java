package com.webshop.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.webshop.order.OrderEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    public PaymentIntent createPaymentIntent(OrderEntity order) throws StripeException {

        long amountInCents = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .putMetadata("orderId", order.getId().toString())
                .build();

        return PaymentIntent.create(params);
    }
}