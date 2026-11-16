package com.webshop.cart;

import com.webshop.user.User;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addProductToCart(Long userId, Long productId, Integer quantity);
    CartResponse updateItemQuantity(Long userId, Long productId, Integer quantity);
    CartResponse removeItemFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
