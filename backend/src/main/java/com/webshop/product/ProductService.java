package com.webshop.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> search(ProductFilterDto filter, Pageable pageable);
    ProductResponse addProduct(ProductCreateRequest productCreateRequest);
    ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest);
    ProductResponse getProductById(Long productId);
    void deleteProduct(Long productId);
    boolean existsByNameIgnoreCase(String name);
}
