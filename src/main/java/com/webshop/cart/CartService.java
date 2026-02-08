package com.webshop.cart;

public interface CartService {
    CartDTO getCart(Long userId);
    void addToCart(Long userId, Long productId, int quantity);
    void updateQuantity(Long userId, Long productId, int quantity);
    void removeFromCart(Long userId, Long productId);
}
