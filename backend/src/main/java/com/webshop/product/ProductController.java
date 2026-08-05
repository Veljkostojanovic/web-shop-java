package com.webshop.product;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductDTO>> search(@ParameterObject @Valid ProductFilterDto filter,
                                                   @ParameterObject Pageable pageable) {
        Page<ProductDTO> page = productService.search(filter, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO){
        ProductDTO newProductDTO = productService.addProduct(productDTO);
        return ResponseEntity.ok(newProductDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id){
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok().body(productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Void> productExistsByNameIgnoreCase(@RequestParam String name){
        if(!productService.existsByNameIgnoreCase(name)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.ok().body(productService.updateProduct(id, productDTO));
    }
}
