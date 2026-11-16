package com.webshop.category;

import com.webshop.common.dto.ExistsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable @Positive Long id) {
        CategoryResponse categoryById = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryById);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(@ParameterObject @Valid Pageable pageable) {
        Page<CategoryResponse> page = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        CategoryResponse savedCategoryResponse = categoryService.addCategory(categoryCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCategoryResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(savedCategoryResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable @Positive Long id,
                                                           @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest) {
        CategoryResponse updated = categoryService.updateCategory(id, categoryUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<ExistsResponse> categoryExistsByNameIgnoreCase(@RequestParam @NotBlank @Size(max = 255) String name) {
        return ResponseEntity.ok(new ExistsResponse(categoryService.existsByNameIgnoreCase(name)));
    }
}
