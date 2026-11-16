package com.webshop.product;

import com.webshop.common.dto.ExistsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> searchProducts(@ParameterObject @Valid ProductFilterDto filter,
                                                                @ParameterObject Pageable pageable) {
        Page<ProductResponse> page = productService.search(filter, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @Positive Long id){
        ProductResponse productResponse = productService.getProductById(id);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest){
        ProductResponse created = productService.addProduct(productCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable @Positive Long id,
                                                         @RequestBody @Valid ProductUpdateRequest productUpdateRequest){
        return ResponseEntity.ok().body(productService.updateProduct(id, productUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable @Positive Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<ExistsResponse> productExistsByNameIgnoreCase(@RequestParam
                                                                            @NotBlank
                                                                            @Size(max = 255)
                                                                            String name){
        return ResponseEntity.ok(new ExistsResponse(productService.existsByNameIgnoreCase(name)));
    }
}
