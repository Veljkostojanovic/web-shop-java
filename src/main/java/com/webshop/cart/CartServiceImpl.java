package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    @Override
    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity) {
        if(quantity <= 0){
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart doesnt exists"));
        Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream().filter(item -> item.getProductId().equals(product.getId())).findFirst();

        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            int newTotalQuantity = item.getQuantity() + quantity;
            validateStock(product, newTotalQuantity);
            item.setQuantity(newTotalQuantity);
        }
        else{
            validateStock(product, quantity);
            CartItem cartItem = new CartItem(cart, productId, quantity);
            cart.getItems().add(cartItem);
        }
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long productId, int quantity) {
        if(quantity < 0){
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        if(quantity == 0){
            removeItemFromCart(userId,  productId);
            return;
        }

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Product product =  productRepository.findById(productId)
                .orElseThrow( () -> new ResourceNotFoundException("Product not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        validateStock(product, quantity);
        existingItem.setQuantity(quantity);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        Cart cart =  cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock for product: " + product.getName());
        }
    }


    public CartDTO toDto(Cart cart) {
        if (cart == null) return null;

        List<Long> productIds = cart.getItems().stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    Product p = productMap.get(item.getProductId());
                    return mapToItemDto(item, p);
                })
                .toList();

        CartDTO dto = new CartDTO();
        dto.setItems(itemDTOs);
        dto.setTotalPrice(itemDTOs.stream()
                .map(CartItemDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }

    private CartItemDTO mapToItemDto(CartItem item, Product p) {
        if (p == null) throw new ResourceNotFoundException("Product not found");

        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(p.getId());
        dto.setProductName(p.getName());
        dto.setProductPrice(p.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return dto;
    }
}
