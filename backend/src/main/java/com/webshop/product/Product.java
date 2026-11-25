package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.common.exceptions.DomainValidationException;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.persistence.AuditableEntity;
import com.webshop.common.util.TextNormalize;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int PRICE_PRECISION = 19;
    public static final int PRICE_SCALE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(nullable = false, precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public Product(String name, String description, BigDecimal price, Integer stock, Category category) {
        rename(name);
        changeDescription(description);
        changePrice(price);
        this.stock = requireValidInitialStock(stock);
        changeCategory(category);
        this.status = ProductStatus.ACTIVE;
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("Quantity must be positive");
        }
    }

    private static int requireValidInitialStock(Integer stock) {
        if (stock == null) {
            throw new DomainValidationException("Stock is required");
        }
        if (stock < 0) {
            throw new DomainValidationException("Stock cannot be negative");
        }
        return stock;
    }

    public void rename(String name) {
        String normalized = TextNormalize.normalize(name);

        if (normalized == null || normalized.isBlank()) {
            throw new DomainValidationException("Product name cannot be blank");
        }

        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException("Product name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }

        this.name = normalized;
    }

    public void changeDescription(String description) {
        String cleaned = description == null ? null : description.strip();
        if (cleaned != null && cleaned.isEmpty()) {
            cleaned = null;
        }
        if (cleaned != null && cleaned.length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainValidationException("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        this.description = cleaned;
    }

    public void changePrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new DomainValidationException("Price must be positive");
        }

        final BigDecimal normalized;

        try {
            normalized = price.setScale(PRICE_SCALE, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new DomainValidationException(
                    "Price must be representable with two decimal places");
        }

        if (normalized.precision() > PRICE_PRECISION) {
            throw new DomainValidationException("Price exceeds supported range");
        }

        this.price = normalized;
    }

    public void changeCategory(Category category) {
        if (category == null) {
            throw new DomainValidationException("Category cannot be null");
        }
        if (!category.isActive())
            throw new ResourceConflictException("Category " + category.getId() + " is archived");

        this.category = category;
    }

    public void decreaseStock(int quantity) {
        requireActive();
        requirePositive(quantity);
        if (quantity > stock) {
            throw new InsufficientStockException(id, name, quantity, stock);
        }
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        requirePositive(quantity);
        if (quantity > Integer.MAX_VALUE - stock) {
            throw new DomainValidationException("Stock would exceed the maximum supported value");
        }
        stock += quantity;
    }

    public void adjustStock(int delta) {
        if (delta == 0) {
            throw new DomainValidationException("Stock adjustment cannot be zero");
        }
        long result = (long) stock + delta;
        if (result < 0) {
            throw new DomainValidationException("Stock cannot go below zero (current stock: " + stock + ")");
        }
        if (result > Integer.MAX_VALUE) {
            throw new DomainValidationException("Stock would exceed the maximum supported value");
        }
        stock = (int) result;
    }

    public void archive() {
        this.status = ProductStatus.ARCHIVED;
    }

    public void activate() {
        if (!category.isActive()) {
            throw new ResourceConflictException(
                    "Product " + id + " cannot be activated because its category is archived");
        }
        this.status = ProductStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

    private void requireActive() {
        if (!isActive()) {
            throw new ResourceConflictException("Product " + id + " is archived and cannot be sold");
        }
    }
}
