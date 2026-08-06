package com.webshop.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<OrderEntity> findById(@Param("id") Long id);

    Page<OrderEntity> findAll(Specification<OrderEntity> spec, Pageable pageable);

    @Query("    SELECT DISTINCT o\n" +
            "    FROM OrderEntity o\n" +
            "    LEFT JOIN FETCH o.orderItems oi\n" +
            "    LEFT JOIN FETCH oi.product\n" +
            "    WHERE o.id = :orderId\n" +
            "      AND o.user.id = :userId")
    Optional<OrderEntity> findByIdAndUserId(Long orderId, Long userId);
}
