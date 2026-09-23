package com.webshop.category;

import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse addCategory(CategoryCreateRequest categoryCreateRequest) {
        log.info("Addind category with name = {}", categoryCreateRequest.name());
        if(categoryRepository.existsByNameIgnoreCase(categoryCreateRequest.name()))throw new ResourceConflictException("Category already exists");

        Category category = CategoryMapper.toEntity(categoryCreateRequest);
        Category saved =  categoryRepository.save(category);
        log.info("Added category with categoryId = {} and categoryName = {}", category.getId(), category.getName());
        return CategoryMapper.toDTO(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        log.info("Updating category with categoryId = {}", categoryId);
        Category existing = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        existing.setName(categoryUpdateRequest.name());

        Category updated = categoryRepository.save(existing);
        log.info("Updated category with categoryId = {} and categoryName = {}", categoryId, categoryUpdateRequest.name());
        return  CategoryMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository
                            .findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return CategoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId))throw new ResourceNotFoundException("Category not found");

        categoryRepository.deleteById(categoryId);
        log.info("Deleted category with categoryId = {}", categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}
