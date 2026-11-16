package com.webshop.cart;

import com.webshop.common.exceptions.CartItemNotFoundException;
import com.webshop.product.Product;
import com.webshop.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> items = new ArrayList<>();

    public Cart(User user) {
        changeUser(user);
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }


        Optional<CartItem> existing = findItem(product.getId());
        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
        } else {
            items.add(new CartItem(this, product, quantity));
        }
    }

    public void updateItem(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        CartItem.validateQuantity(quantity);
        CartItem item = findItem(productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));
        item.changeQuantity(quantity);
    }

    public void deleteItem(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        Iterator<CartItem> iterator = items.iterator();

        while(iterator.hasNext()){
            CartItem item = iterator.next();
            if(item.getProduct().getId().equals(productId)){
                items.remove(item);
                return;
            }
        }
    }

    public void clear() {
        Iterator<CartItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            iterator.remove();
            item.detachFromCart();
        }
    }

    private Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(item -> productId.equals(item.getProduct().getId()))
                .findFirst();
    }

    private void changeUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }
}
