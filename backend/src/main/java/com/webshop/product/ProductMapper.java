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
                CategoryMapper.toDTO(product.getCategory())
        );
    }

    public static Product toEntity(ProductResponse dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setStock(dto.stock());
        product.setPrice(dto.price());
        return product;
    }

    public static Product toEntity(ProductCreateRequest request) {
        if (request == null) return null;
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return product;
    }
}
