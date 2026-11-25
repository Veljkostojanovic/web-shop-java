package com.webshop.cart;

public interface CartService {
    CartResponse getCart(Long userId);

    CartResponse addProductToCart(Long userId, Long productId, int quantity);

    CartResponse updateItemQuantity(Long userId, Long productId, int quantity);

    CartResponse removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
