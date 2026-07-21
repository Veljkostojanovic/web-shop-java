package com.webshop.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDTO> search(ProductFilterDto filter, Pageable pageable);
    ProductDTO addProduct(ProductDTO product);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO getProductById(Long productId);
    boolean deleteProduct(Long productId);
    boolean existsByNameIgnoreCase(String name);
}
