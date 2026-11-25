package com.webshop.cart;

import com.webshop.common.exceptions.CartItemNotFoundException;
import com.webshop.common.exceptions.DomainValidationException;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.persistence.AuditableEntity;
import com.webshop.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "carts")
public class Cart extends AuditableEntity {

    public static final int MAX_DISTINCT_ITEMS = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id")
    private final List<CartItem> items = new ArrayList<>();

    public Cart(Long userId) {
        if (userId == null) {
            throw new DomainValidationException("User ID is required");
        }
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(Product product, int quantity) {
        requirePurchasable(product);
        CartItem.requireValidQuantity(quantity);

        Optional<CartItem> existing = findItem(product.getId());
        int newQuantity = existing.map(item -> item.getQuantity() + quantity).orElse(quantity);

        if (newQuantity > CartItem.MAX_QUANTITY) {
            throw new DomainValidationException("You can have at most " + CartItem.MAX_QUANTITY
                    + " of '" + product.getName() + "' in your cart");
        }
        requireInStock(product, newQuantity);

        if (existing.isPresent()) {
            existing.get().changeQuantity(newQuantity);
            return;
        }
        if (items.size() >= MAX_DISTINCT_ITEMS) {
            throw new DomainValidationException(
                    "A cart can contain at most " + MAX_DISTINCT_ITEMS + " different products");
        }
        items.add(new CartItem(this, product, newQuantity));
    }

    public void changeItemQuantity(Long productId, int quantity) {
        CartItem item = findItem(productId).orElseThrow(() -> new CartItemNotFoundException(productId));
        CartItem.requireValidQuantity(quantity);
        requirePurchasable(item.getProduct());
        requireInStock(item.getProduct(), quantity);
        item.changeQuantity(quantity);
    }

    public boolean removeItem(Long productId) {
        return items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    private Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    private static void requirePurchasable(Product product) {
        if (product == null) {
            throw new DomainValidationException("Product is required");
        }
        if (!product.isActive()) {
            throw new ResourceConflictException("Product " + product.getId() + " is no longer available");
        }
    }

    private static void requireInStock(Product product, int requested) {
        if (requested > product.getStock()) {
            throw new InsufficientStockException(product.getId(), product.getName(), requested, product.getStock());
        }
    }
}
