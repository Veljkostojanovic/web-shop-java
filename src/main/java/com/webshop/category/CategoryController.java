package com.webshop.category;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody  CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.addCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable long id){
        CategoryDTO categoryById = categoryService.getCategoryById(id);
        return ResponseEntity.ok().body(categoryById);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
        List<CategoryDTO> categoryDTOs = categoryService.getAllCategories();
        return ResponseEntity.ok().body(categoryDTOs);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategoryById(@RequestParam long id){
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
