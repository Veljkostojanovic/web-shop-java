package com.webshop.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
            select case when count(c) > 0 then true else false end
            from Category c
            where lower(c.name) = lower(:name)
            """)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("""
            select case when count(c) > 0 then true else false end
            from Category c
            where lower(c.name) = lower(:name) and c.id <> :id
            """)
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    Optional<Category> findByIdAndStatus(Long id, CategoryStatus status);

    Page<Category> findAllByStatus(CategoryStatus status, Pageable pageable);
}
