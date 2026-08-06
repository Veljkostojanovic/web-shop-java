package com.webshop.order;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<OrderEntity> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null)
                return null;

            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, cb) -> {
            if (status == null)
                return null;

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<OrderEntity> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) -> {
            if (startDate == null)
                return null;

            return cb.greaterThanOrEqualTo(root.get("orderDate"), startDate);
        };
    }

    public static Specification<OrderEntity> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (endDate == null)
                return null;

            return cb.lessThanOrEqualTo(root.get("orderDate"), endDate);
        };
    }

    public static Specification<OrderEntity> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null)
                return null;

            return cb.greaterThanOrEqualTo(root.get("totalAmount"), minPrice);
        };
    }

    public static Specification<OrderEntity> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null)
                return null;

            return cb.lessThanOrEqualTo(root.get("totalAmount"), maxPrice);
        };
    }

    public static Specification<OrderEntity> buildSpecification(OrderFilter filter) {
        Specification<OrderEntity> specification = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Fetch<OrderEntity, OrderItem> itemsFetch = root.fetch("orderItems", JoinType.LEFT);
                itemsFetch.fetch("product", JoinType.LEFT);

                query.distinct(true);
            }

            return cb.conjunction();
        };

        if (filter == null) return specification;

        if (filter.getUserId() != null) {
            specification = specification.and(hasUserId(filter.getUserId()));
        }

        if (filter.getStatus() != null) {
            specification = specification.and(hasStatus(filter.getStatus()));
        }

        if (filter.getStartDate() != null) {
            specification = specification.and(createdAfter(filter.getStartDate()));
        }

        if (filter.getEndDate() != null) {
            specification = specification.and(createdBefore(filter.getEndDate()));
        }

        if (filter.getMinPrice() != null) {
            specification = specification.and(priceGreaterThanOrEqual(filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            specification = specification.and(priceLessThanOrEqual(filter.getMaxPrice()));
        }

        return specification;
    }
}