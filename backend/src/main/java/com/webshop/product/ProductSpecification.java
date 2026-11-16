package com.webshop.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name){
        return (root, query, cb) ->
                cb.like( cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> minPrice(BigDecimal price){
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Product> maxPrice(BigDecimal price){
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Product> minStock(Integer stock){
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("stock"), stock);
    }

    private static Specification<Product> hasCategory(Long id) {
        return id == null ? null
                : (root, q, cb) -> cb.equal(root.get("category").get("id"), id);
    }

    public static Specification<Product> buildSpecification(ProductFilterDto filter){
        Specification<Product> specification = (root, query, cb) -> cb.conjunction();

        if (filter == null) return specification;

        if(filter.name() != null && !filter.name().isBlank()){
            specification = specification.and(ProductSpecification.hasName(filter.name()));
        }

        if(filter.categoryId() != null){
            specification = specification.and(ProductSpecification.hasCategory(filter.categoryId()));
        }

        if(filter.maxPrice() != null){
            specification = specification.and(ProductSpecification.maxPrice(filter.maxPrice()));
        }

        if(filter.minPrice() != null){
            specification = specification.and(ProductSpecification.minPrice(filter.minPrice()));
        }

        if(filter.minStock() != null){
            specification = specification.and(ProductSpecification.minStock(filter.minStock()));
        }
        return specification;
    }
}
