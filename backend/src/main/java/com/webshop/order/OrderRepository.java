package com.webshop.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<OrderEntity> findById(@Param("id") Long id);

    @Query("SELECT o from OrderEntity o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId")
    List<OrderEntity> findByUserId(@Param("userId") Long userId);
}
