package com.webshop.cart;

import com.webshop.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_items_cart_product",
                columnNames = {"cart_id", "product_id"}))
@Check(
        name = "ck_cart_items_quantity_range",
        constraints = "quantity >= 1 AND quantity <= 300"
)
public class CartItem {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    @Column(nullable = false)
    private Long version;

    CartItem(Cart cart, Product product, int quantity) {
        changeQuantity(quantity);
        changeCart(cart);
        changeProduct(product);
    }

    void changeQuantity(Integer quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    void changeCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        this.cart = cart;
    }

    void changeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        this.product = product;
    }

    void increaseQuantity(Integer amount) {
        if(amount + quantity > MAX_QUANTITY) {
            throw new  IllegalArgumentException(amount + " is greater than " + MAX_QUANTITY);
        }
        validateQuantity(amount);
        changeQuantity(Math.addExact(this.quantity, amount));
    }

    void detachFromCart() {
        this.cart = null;
    }

    static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > 300) {
            throw new IllegalArgumentException("Quantity must be between " + MIN_QUANTITY + " and " + MAX_QUANTITY);
        }
    }
}
