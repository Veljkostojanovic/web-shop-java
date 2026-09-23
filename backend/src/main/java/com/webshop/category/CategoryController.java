package com.webshop.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryCreateRequest categoryCreateRequest){
        CategoryResponse savedCategoryResponse = categoryService.addCategory(categoryCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable long id){
        CategoryResponse categoryById = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryById);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest){
        CategoryResponse savedCategoryResponse = categoryService.updateCategory(id, categoryUpdateRequest);
        return ResponseEntity.ok(savedCategoryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Void> categoryExistsByNameIgnoreCase(@RequestParam String name) {
        if(!categoryService.existsByNameIgnoreCase(name)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
