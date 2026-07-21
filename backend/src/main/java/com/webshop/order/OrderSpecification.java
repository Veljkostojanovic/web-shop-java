package com.webshop.order;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<OrderEntity> buildSpecification(OrderFilterDto filter) {
        return (root, query, cb) -> {

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Fetch<OrderEntity, OrderItem> itemsFetch = root.fetch("orderItems", JoinType.LEFT);
                itemsFetch.fetch("product", JoinType.LEFT);

                query.distinct(true);
            }

            Predicate predicate = cb.conjunction();

            if (filter.getUserId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), filter.getUserId()));
            }
            if (filter.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getStartDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("orderDate"), filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("orderDate"), filter.getEndDate()));
            }
            if (filter.getMinPrice() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("totalAmount"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("totalAmount"), filter.getMaxPrice()));
            }

            return predicate;
        };
    }

    private static Specification<OrderEntity> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<OrderEntity> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) -> startDate == null ? null : cb.greaterThanOrEqualTo(root.get("orderDate"), startDate);
    }

    private static Specification<OrderEntity> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) -> endDate == null ? null : cb.lessThanOrEqualTo(root.get("orderDate"), endDate);
    }

    private static Specification<OrderEntity> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("totalAmount"), minPrice);
    }

    private static Specification<OrderEntity> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("totalAmount"), maxPrice);
    }
}