package com.webshop.product;

import com.webshop.category.CategoryMapper;

public final class ProductMapper {

    private ProductMapper() {

    }

    public static ProductResponse toResponse(Product product) {
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

    public static ProductAdminResponse toAdminResponse(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductAdminResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                CategoryMapper.toResponse(product.getCategory()),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
