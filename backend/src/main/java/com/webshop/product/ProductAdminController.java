package com.webshop.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductAdminResponse>> searchProducts(@ParameterObject @Valid AdminProductFilter filter,
                                                                     @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.adminSearch(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminResponse> getProductById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.getProductForAdmin(id));
    }
}