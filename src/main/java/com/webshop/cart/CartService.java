package com.webshop.cart;

public interface CartService {
    Cart getCartByUserId(Long userId);
    void addItemToCart(Long userId, Long productId, int quantity);
    void updateItemQuantity(Long userId, Long productId, int quantity);
    void removeItemFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
