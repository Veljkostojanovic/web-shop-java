package com.webshop.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(Long id);

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);

    @Query("""
            select case when count(p) > 0 then true else false end
            from Product p
            where lower(p.name) = lower(:name)
            """)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("""
            select case when count(p) > 0 then true else false end
            from Product p
            where lower(p.name) = lower(:name) and p.id <> :id
            """)
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    boolean existsByCategoryIdAndStatus(Long categoryId, ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}
