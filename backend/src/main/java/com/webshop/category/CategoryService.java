package com.webshop.category;

import java.util.List;

public interface CategoryService {
    CategoryResponse addCategory(CategoryCreateRequest categoryCreateRequest);
    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);
    CategoryResponse getCategoryById(Long categoryId);
    List<CategoryResponse> getAllCategories();
    void deleteCategory(Long categoryId);
    boolean existsByNameIgnoreCase(String name);
}
