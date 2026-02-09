package com.webshop.product;

import java.util.List;

public interface ProductService {
    ProductDTO addProduct(ProductDTO product);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> findByCategoryId(Long categoryId);
    List<ProductDTO> findByNameContainingIgnoreCase(String name);
    boolean deleteProduct(Long productId);
    boolean existsByNameIgnoreCase(String name);
}
