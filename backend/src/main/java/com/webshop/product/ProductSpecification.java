package com.webshop.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

final class ProductSpecification {

    private static final char LIKE_ESCAPE = '\\';

    private ProductSpecification() {
    }

    static Specification<Product> forCatalog(ProductFilterDto filter) {
        Specification<Product> spec = hasStatus(ProductStatus.ACTIVE);
        if (filter == null) {
            return spec;
        }
        return withCommonFilters(spec, filter.name(), filter.categoryId(),
                filter.minPrice(), filter.maxPrice(), filter.minStock());
    }

    static Specification<Product> forAdmin(AdminProductFilter filter) {
        Specification<Product> spec = allProducts();
        if (filter == null) {
            return spec;
        }
        if (filter.status() != null) {
            spec = spec.and(hasStatus(filter.status()));
        }
        return withCommonFilters(spec, filter.name(), filter.categoryId(),
                filter.minPrice(), filter.maxPrice(), filter.minStock());
    }

    private static Specification<Product> withCommonFilters(Specification<Product> spec,
                                                            String name,
                                                            Long categoryId,
                                                            BigDecimal minPrice,
                                                            BigDecimal maxPrice,
                                                            Integer minStock) {
        if (name != null && !name.isBlank()) {
            spec = spec.and(nameContains(name));
        }
        if (categoryId != null) {
            spec = spec.and(hasCategory(categoryId));
        }
        if (minPrice != null) {
            spec = spec.and(priceAtLeast(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(priceAtMost(maxPrice));
        }
        if (minStock != null) {
            spec = spec.and(stockAtLeast(minStock));
        }
        return spec;
    }

    private static Specification<Product> allProducts() {
        return (root, query, cb) -> cb.conjunction();
    }

    private static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Product> nameContains(String name) {
        String pattern = "%" + escapeLike(name.toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern, LIKE_ESCAPE);
    }

    private static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> priceAtLeast(BigDecimal price) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), price);
    }

    private static Specification<Product> priceAtMost(BigDecimal price) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), price);
    }

    private static Specification<Product> stockAtLeast(Integer stock) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), stock);
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}