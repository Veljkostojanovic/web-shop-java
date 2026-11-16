package com.webshop.common.exceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long productId) {
        super("Product " + productId + " is not in the cart");
    }
}
