package com.webshop.cart;

import com.webshop.common.exceptions.DomainValidationException;
import com.webshop.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart_items")
public class CartItem {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false, updatable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    CartItem(Cart cart, Product product, int quantity) {
        this.cart = Objects.requireNonNull(cart, "cart");
        this.product = Objects.requireNonNull(product, "product");
        changeQuantity(quantity);
    }

    static void requireValidQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new DomainValidationException(
                    "Quantity must be between " + MIN_QUANTITY + " and " + MAX_QUANTITY);
        }
    }

    void changeQuantity(int quantity) {
        requireValidQuantity(quantity);
        this.quantity = quantity;
    }

    public BigDecimal lineTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public boolean isPurchasable() {
        return product.isActive() && quantity <= product.getStock();
    }
}
