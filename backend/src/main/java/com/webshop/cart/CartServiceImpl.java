package com.webshop.cart;

import com.webshop.common.exceptions.CartItemNotFoundException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .map(CartMapper::toResponse)
                .orElseGet(CartMapper::emptyResponse);

    }

    @Override
    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = lockOrCreateCart(userId);
        Product product = productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        cart.addItem(product, quantity);

        log.info("Cart item added: userId={}, productId={}, quantity={}", userId, productId, quantity);
        return CartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = lockExistingCart(userId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        cart.changeItemQuantity(productId, quantity);

        log.info("Cart item updated: userId={}, productId={}, quantity={}", userId, productId, quantity);
        return CartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        Optional<Cart> cart = lockExistingCart(userId);
        if (cart.isEmpty()) {
            return CartMapper.emptyResponse();
        }

        if (cart.get().removeItem(productId)) {
            log.info("Cart item removed: userId={}, productId={}", userId, productId);
        }
        return CartMapper.toResponse(cart.get());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        lockExistingCart(userId).ifPresent(cart -> {
            cart.clear();
            log.info("Cart cleared: userId={}", userId);
        });
    }

    private Cart lockOrCreateCart(Long userId) {
        if (cartRepository.findByUserIdForUpdate(userId).isEmpty()) {
            cartRepository.insertIfAbsent(userId);
            cartRepository.findByUserIdForUpdate(userId)
                    .orElseThrow(() -> new IllegalStateException("Cart for user " + userId + " could not be created"));
        }
        return loadItems(userId);
    }

    private Optional<Cart> lockExistingCart(Long userId) {
        return cartRepository.findByUserIdForUpdate(userId)
                .map(locked -> loadItems(userId));
    }

    private Cart loadItems(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Locked cart for user " + userId + " disappeared"));
    }
}