package com.webshop.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse addCategory(CategoryCreateRequest categoryCreateRequest);

    Page<CategoryAdminResponse> adminSearch(CategoryStatus status, Pageable pageable);

    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);

    CategoryResponse getCategoryById(Long categoryId);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    void deleteCategory(Long categoryId);

    CategoryResponse activateCategory(Long categoryId);

    boolean existsByNameIgnoreCase(String name);
}
