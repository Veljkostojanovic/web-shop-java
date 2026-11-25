package com.webshop.order;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

final class OrderSpecification {

    private OrderSpecification() {
    }

    static Specification<OrderEntity> forCustomer(Long userId, OrderFilter filter) {
        Specification<OrderEntity> spec = belongsTo(userId);
        if (filter == null) {
            return spec;
        }
        return withCommonFilters(spec, filter.status(), filter.createdFrom(), filter.createdTo(),
                filter.minTotal(), filter.maxTotal());
    }

    static Specification<OrderEntity> forAdmin(AdminOrderFilter filter) {
        Specification<OrderEntity> spec = allOrders();
        if (filter == null) {
            return spec;
        }
        if (filter.userId() != null) {
            spec = spec.and(belongsTo(filter.userId()));
        }
        return withCommonFilters(spec, filter.status(), filter.createdFrom(), filter.createdTo(),
                filter.minTotal(), filter.maxTotal());
    }

    private static Specification<OrderEntity> withCommonFilters(Specification<OrderEntity> spec,
                                                                OrderStatus status,
                                                                Instant createdFrom,
                                                                Instant createdTo,
                                                                BigDecimal minTotal,
                                                                BigDecimal maxTotal) {
        if (status != null) {
            spec = spec.and(hasStatus(status));
        }
        if (createdFrom != null) {
            spec = spec.and(createdAtOrAfter(createdFrom));
        }
        if (createdTo != null) {
            spec = spec.and(createdBefore(createdTo));
        }
        if (minTotal != null) {
            spec = spec.and(totalAtLeast(minTotal));
        }
        if (maxTotal != null) {
            spec = spec.and(totalAtMost(maxTotal));
        }
        return spec;
    }

    private static Specification<OrderEntity> allOrders() {
        return (root, query, cb) -> cb.conjunction();
    }

    private static Specification<OrderEntity> belongsTo(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<OrderEntity> createdAtOrAfter(Instant from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    private static Specification<OrderEntity> createdBefore(Instant to) {
        return (root, query, cb) -> cb.lessThan(root.get("createdAt"), to);
    }

    private static Specification<OrderEntity> totalAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalAmount"), min);
    }

    private static Specification<OrderEntity> totalAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalAmount"), max);
    }
}