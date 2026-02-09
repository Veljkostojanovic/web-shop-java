package com.webshop.cart;

public interface CartService {

    CartDTO getCartForUser(Long userId);

    CartDTO addItemToCart(Long userId, Long productId, int quantity);

    CartDTO updateItemQuantity(Long userId, Long productId, int quantity);

    CartDTO removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
