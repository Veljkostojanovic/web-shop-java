package com.webshop.cart;

import com.webshop.product.ProductService;
import com.webshop.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;

    @Override
    public CartDTO getOrCreateCart(Long userId) {
        return null;
    }

    @Override
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {
        return null;
    }

    @Override
    public CartDTO updateItemQuantity(Long userId, Long productId, int quantity) {
        return null;
    }

    @Override
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        return null;
    }

    @Override
    public void clearCart(Long userId) {

    }

    private Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> createEmptyCart(userId));
    }

    private Cart createEmptyCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cart;
    }
}
