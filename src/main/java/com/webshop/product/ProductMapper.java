package com.webshop.product;

import com.webshop.category.CategoryMapper;

public class ProductMapper {

    public static ProductDTO  toDTO(Product product) {
        if(product == null) return null;
        ProductDTO productDTO = new ProductDTO();

        // name, desc, price, stock, mapp cat->dto
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCategory(CategoryMapper.toDTO(product.getCategory()));
        return productDTO;
    }

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        return product;
    }
}
