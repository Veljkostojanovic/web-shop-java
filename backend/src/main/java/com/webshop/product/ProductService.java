package com.webshop.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> search(ProductFilterDto filter, Pageable pageable);

    Page<ProductAdminResponse> adminSearch(AdminProductFilter filter, Pageable pageable);

    ProductAdminResponse getProductForAdmin(Long productId);

    ProductResponse addProduct(ProductCreateRequest productCreateRequest);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest);

    ProductResponse adjustStock(Long productId, int delta);

    ProductResponse activateProduct(Long productId);

    ProductResponse getProductById(Long productId);

    void deleteProduct(Long productId);

    boolean existsByNameIgnoreCase(String name);
}
