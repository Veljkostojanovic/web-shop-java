package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public CartDTO getOrCreateCart(Long userId) {
        log.info("Getting cart for userId = {}", userId);
        Cart cart = getCartEntity(userId);
        return mapToDTO(cart);
    }

    @Override
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {
        log.info("Adding product = {}, userId = {}, quantity = {}",  productId, userId, quantity);
        Cart cart = getCartEntity(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProductId(productId);
                            newItem.setQuantity(quantity);
                            cart.getItems().add(newItem);
                        }
                );
        Cart savedCart = cartRepository.save(cart);

        log.info("Added product = {}, userId = {}, quantity = {}",  productId, userId, quantity);
        return mapToDTO(savedCart);
    }

    @Override
    public CartDTO updateItemQuantity(Long userId, Long productId, int quantity) {
        log.info("Updating product = {}, userId = {}, quantity = {}", productId, userId, quantity);
        Cart cart = getCartEntity(userId);

        CartItem cartItem = cart.getItems().stream()
                        .filter(item -> item.getProductId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if(quantity <= 0)
            cart.getItems().remove(cartItem);
        else
            cartItem.setQuantity(quantity);

        log.info("Updated item, productId = {}, userId = {}, quantity = {}", productId, userId, quantity);
        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        log.info("Removing product = {}, userId = {}", productId, userId);
        Cart cart = getCartEntity(userId);

        CartItem cartItem = cart.getItems()
                        .stream()
                        .filter(item -> item.getProductId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        cart.getItems().remove(cartItem);
        Cart savedCart = cartRepository.save(cart);
        log.info("Removed product = {}, userId = {}", productId, userId);
        return mapToDTO(savedCart);
    }

    @Override
    public void clearCart(Long userId) {
        log.info("Clearing cart = {}", userId);
        cartRepository.findByUserId(userId)
                .ifPresent( cart -> {
                    cart.getItems().clear();
                    cartRepository.save(cart);
                });
    }

    private Cart getCartEntity(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }


    private CartDTO mapToDTO(Cart cart) {
        List<Long> productIds = cart.getItems().stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    Product product = productMap.get(item.getProductId());

                    BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemDTO(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            price
                    );
                }).toList();
        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(itemDTOs, totalPrice);
    }
}
