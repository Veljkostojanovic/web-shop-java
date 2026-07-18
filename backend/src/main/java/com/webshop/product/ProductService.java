package com.webshop.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDTO> search(ProductFilterDto filter, Pageable pageable);
    ProductDTO addProduct(ProductDTO product);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> getAllProducts();
    Page<ProductDTO> getAllProducts(Pageable pageable);
    List<ProductDTO> findByCategoryId(Long categoryId);
    List<ProductDTO> findByNameContainingIgnoreCase(String name);
    boolean deleteProduct(Long productId);
    boolean existsByNameIgnoreCase(String name);
}
