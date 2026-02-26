package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @Override
    @Transactional(readOnly = true)
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if(cart == null){
            cart = new Cart();
            cart.setUserId(userId);
            return cart;
        }
        return cart;
    }

    @Override
    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity) {
        if(quantity <= 0){
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        Cart cart = cartRepository.findByUserId(userId);
        Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream().filter(item -> item.getProductId().equals(product.getId())).findFirst();

        if(existingItem.isPresent()){
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        }
        else{
            validateStock(product, quantity);
            CartItem cartItem = new CartItem(cart, productId, quantity);
            cart.getItems().add(cartItem);
        }
    }

    @Override
    public void updateItemQuantity(Long userId, Long productId, int quantity) {
        if(quantity <= 0){
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        if(quantity == 0){
            removeItemFromCart(userId,  productId);
            return;
        }

        Cart cart = cartRepository.findByUserId(userId);

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
        Cart cart =  cartRepository.findByUserId(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        cart.getItems().clear();
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock for product: " + product.getName());
        }
    }
}
