package com.webshop.order;

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
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = Product.MAX_NAME_LENGTH, updatable = false)
    private String productName;

    @Column(nullable = false, updatable = false)
    private int quantity;

    @Column(name = "price", nullable = false, precision = Product.PRICE_PRECISION,
            scale = Product.PRICE_SCALE, updatable = false)
    private BigDecimal unitPrice;

    OrderItem(OrderEntity order, Product product, int quantity) {
        this.order = Objects.requireNonNull(order, "order");
        this.product = Objects.requireNonNull(product, "product");
        if (quantity <= 0) {
            throw new DomainValidationException("Quantity must be positive");
        }
        this.quantity = quantity;
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
