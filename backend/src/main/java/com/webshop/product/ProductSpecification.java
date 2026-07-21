package com.webshop.product;

import com.webshop.category.Category;
import jakarta.persistence.criteria.Join;
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

    public static Specification<Product> hasCategory(Long categoryId){
        return (root, query, cb) -> {
            if(categoryId == null)
                return null;

            Join<Product, Category> categoryJoin = root.join("category");

            return cb.equal(categoryJoin.get("id"), categoryId);
        };
    }

    public static Specification<Product> buildSpecification(ProductFilterDto filter){
        Specification<Product> specification = (root, query, cb) -> cb.conjunction();

        if (filter == null) return specification;

        if(filter.getName() != null){
            specification = specification.and(ProductSpecification.hasName(filter.getName()));
        }

        if(filter.getCategoryId() != null){
            specification = specification.and(ProductSpecification.hasCategory(filter.getCategoryId()));
        }

        if(filter.getMaxPrice() != null){
            specification = specification.and(ProductSpecification.maxPrice(filter.getMaxPrice()));
        }

        if(filter.getMinPrice() != null){
            specification = specification.and(ProductSpecification.minPrice(filter.getMinPrice()));
        }

        if(filter.getStock() != null){
            specification = specification.and(ProductSpecification.minStock(filter.getStock()));
        }
        return specification;
    }
}
