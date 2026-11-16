package com.webshop.product;

import com.webshop.category.CategoryMapper;

public class ProductMapper {

    public static ProductResponse toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                CategoryMapper.toResponse(product.getCategory())
        );
    }
}
