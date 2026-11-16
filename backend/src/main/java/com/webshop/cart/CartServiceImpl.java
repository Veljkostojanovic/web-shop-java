package com.webshop.cart;

import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        requiredId(userId, "User ID");
        Cart cart = findCartWithItems(userId);
        log.info("Get cart for user with id: {}", userId);
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, Integer quantity) {
        requiredId(userId, "User ID");
        requiredId(productId, "Product ID");
        CartItem.validateQuantity(quantity);

        Cart cart = findCartWithItemsForUpdate(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        cart.addItem(product, quantity);

        log.info("Added product = {}, userId = {}, quantity = {}",  productId, userId, quantity);
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long productId, Integer quantity) {
        requiredId(userId, "User ID");
        requiredId(productId, "Product ID");
        CartItem.validateQuantity(quantity);

        Cart cart = findCartWithItemsForUpdate(userId);

        cart.updateItem(productId, quantity);

        log.info("Updated item, productId = {}, userId = {}, quantity = {}", productId, userId, quantity);
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        requiredId(userId, "User ID");
        requiredId(productId, "Product ID");

        Cart cart = findCartWithItemsForUpdate(userId);
        cart.deleteItem(productId);
        log.info("Removed product = {}, userId = {}", productId, userId);
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        requiredId(userId, "User ID");

        Cart cart = lockCart(userId);
        cart.clear();
    }

    private Cart findCartWithItemsForUpdate(Long userId){
        lockCart(userId);
        return findCartWithItems(userId);
    }

    private Cart lockCart(Long userId){
        return cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private Cart findCartWithItems(Long userId){
        requiredId(userId, "User ID");
        return cartRepository
                .findWithItemsByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private void requiredId(Long id, String field){
        if(id == null){
            throw new IllegalArgumentException(field + " is required");
        }
        if(id <= 0){
            throw new IllegalArgumentException(field + " must be positive");
        }
    }

    private CartResponse mapToDTO(Cart cart) {
        if(cart == null)return null;

        List<Long> productIds = cart.getItems().stream()
                .map(item -> item.getProduct().getId())
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemResponse> cartItemsResponse = cart.getItems().stream()
                .map(item -> {
                    Product product = productMap.get(item.getProduct().getId());

                    if(product == null){
                        throw new ResourceNotFoundException("Product not found with id: " + item.getProduct().getId());
                    }

                    BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            price
                    );
                }).toList();
        BigDecimal totalPrice = cartItemsResponse.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cartItemsResponse, totalPrice);
    }
}
