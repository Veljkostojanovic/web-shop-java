package com.webshop.order;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    @EntityGraph(attributePaths = "orderItems")
    Optional<OrderEntity> findWithItemsById(Long id);

    @EntityGraph(attributePaths = "orderItems")
    Optional<OrderEntity> findWithItemsByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderEntity o where o.id = :id and o.user.id = :userId")
    Optional<OrderEntity> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select i.product.id from OrderItem i where i.order.id = :orderId")
    List<Long> findProductIdsByOrderId(@Param("orderId") Long orderId);
}
