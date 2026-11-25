package com.webshop.order;

import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.persistence.AuditableEntity;
import com.webshop.product.Product;
import com.webshop.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "total_amount", nullable = false, precision = Product.PRICE_PRECISION, scale = Product.PRICE_SCALE)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    @OrderBy("id")
    private final List<OrderItem> orderItems = new ArrayList<>();

    public OrderEntity(User user) {
        this.user = Objects.requireNonNull(user, "user");
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO.setScale(Product.PRICE_SCALE);
    }

    public void addLine(Product product, int quantity) {
        if (id != null) {
            throw new ResourceConflictException("Lines can only be added while the order is being placed");
        }
        OrderItem line = new OrderItem(this, product, quantity);
        orderItems.add(line);
        totalAmount = totalAmount.add(line.lineTotal());
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void markPaid() {
        transitionTo(OrderStatus.PAID);
    }

    public void ship() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void deliver() {
        transitionTo(OrderStatus.DELIVERED);
    }

    public void cancel() {
        transitionTo(OrderStatus.CANCELLED);
    }

    public void cancelByCustomer() {
        if (status != OrderStatus.PENDING) {
            throw new ResourceConflictException(
                    "Order " + id + " is " + status + " and can no longer be cancelled by the customer");
        }
        transitionTo(OrderStatus.CANCELLED);
    }

    private void transitionTo(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new ResourceConflictException(
                    "Order " + id + " cannot change from " + status + " to " + target);
        }
        this.status = target;
    }
}
