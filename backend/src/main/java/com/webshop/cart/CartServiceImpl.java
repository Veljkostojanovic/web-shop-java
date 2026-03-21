package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getOrCreateCart(Long userId) {
        Cart cart = getCartEntity(userId);
        return mapToDTO(cart);
    }

    @Override
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {
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
        return mapToDTO(savedCart);
    }

    @Override
    public CartDTO updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getCartEntity(userId);

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent( item-> {
                    if (quantity <= 0) {
                        cart.getItems().remove(item);
                    } else {
                        item.setQuantity(quantity);
                    }
                });

        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartEntity(userId);
        cart.getItems().removeIf(item->item.getProductId().equals(productId));
        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Override
    public void clearCart(Long userId) {
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
