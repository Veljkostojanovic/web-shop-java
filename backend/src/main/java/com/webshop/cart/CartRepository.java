package com.webshop.cart;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO carts (user_id, created_at, updated_at)
            VALUES (:userId, now(), now())
            ON CONFLICT (user_id) DO NOTHING
            """, nativeQuery = true)
    void insertIfAbsent(@Param("userId") Long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cart c where c.userId = :userId")
    Optional<Cart> findByUserIdForUpdate(@Param("userId") Long userId);


    @EntityGraph(attributePaths = {"items", "items.product"})
    @Query("select c from Cart c where c.userId = :userId")
    Optional<Cart> findWithItemsByUserId(@Param("userId") Long userId);

    @Query("select i.product.id from CartItem i where i.cart.userId = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
