package com.webshop.category;

import com.webshop.common.exceptions.DomainValidationException;
import com.webshop.common.persistence.AuditableEntity;
import com.webshop.common.util.TextNormalize;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "categories")
public class Category extends AuditableEntity {

    public static final int MAX_NAME_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public Category(String name) {
        rename(name);
        this.status = CategoryStatus.ACTIVE;
    }

    public void rename(String name) {
        String normalized = TextNormalize.normalize(name);
        if (normalized == null || normalized.isBlank()) {
            throw new DomainValidationException("Category name cannot be blank");
        }
        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException(
                    "Category name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
        this.name = normalized;
    }

    public void archive() {
        this.status = CategoryStatus.ARCHIVED;
    }

    public void activate() {
        this.status = CategoryStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == CategoryStatus.ACTIVE;
    }
}
