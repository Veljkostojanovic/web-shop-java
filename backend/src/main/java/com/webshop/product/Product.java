package com.webshop.product;

import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.util.TextNormalize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.webshop.category.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @NotBlank(message = "Product name cannot be blank")
    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Version
    @Column(nullable = false)
    private Long version;

    public Product(String name, String description, BigDecimal price, Integer stock, Category category) {
        rename(name);
        changeDescription(description);
        changePrice(price);
        changeStock(stock);
        changeCategory(category);
    }

    public void rename(String name){
        name = TextNormalize.normalize(name);

        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Product name cannot be blank");

        if(name.length() > 255)
            throw new IllegalArgumentException("Product name cannot exceed 255 characters");

        this.name = name;
    }

    public void changeDescription(String description) {
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 2000 characters");
        }

        this.description = description;
    }

    public void changePrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        final BigDecimal normalized;

        try {
            normalized = price.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(
                    "Price must be representable with two decimal places", ex);
        }

        if (normalized.precision() > 19) {
            throw new IllegalArgumentException("Price exceeds supported range");
        }

        this.price = normalized;
    }

    public void changeStock(Integer stock) {
        if(stock == null){
            throw new IllegalArgumentException("Stock is required");
        }

        if(stock < 0){
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        this.stock = stock;
    }

    public void changeCategory(Category category) {
        if(category == null){
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = category;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > stock) {
            throw new InsufficientStockException("Insufficient stock");
        }

        stock -= quantity;
    }
}
