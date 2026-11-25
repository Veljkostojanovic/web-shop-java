package com.webshop.common.exceptions;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(Long productId, String productName, int requested, int available) {
        super("Not enough stock for: " + productName
                + " (requested " + requested + ", available " + available + ")");
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }
}