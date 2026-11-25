package com.webshop.product;

import com.webshop.common.dto.ExistsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> searchProducts(@ParameterObject @Valid ProductFilterDto filter,
                                                                @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.search(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        ProductResponse created = productService.addProduct(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable @Positive Long id,
                                                         @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PostMapping("/{id}/stock-adjustments")
    public ResponseEntity<ProductResponse> adjustStock(@PathVariable @Positive Long id,
                                                       @RequestBody @Valid StockAdjustmentRequest request) {
        return ResponseEntity.ok(productService.adjustStock(id, request.delta()));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.activateProduct(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable @Positive Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<ExistsResponse> productExistsByNameIgnoreCase(
            @RequestParam @NotBlank @Size(max = Product.MAX_NAME_LENGTH) String name) {
        return ResponseEntity.ok(new ExistsResponse(productService.existsByNameIgnoreCase(name)));
    }
}