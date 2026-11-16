package com.webshop.category;

import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.util.TextNormalize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse addCategory(CategoryCreateRequest categoryCreateRequest) {
        if(categoryCreateRequest == null){
            throw new BadRequestException("Category request is required");
        }

        String name = categoryCreateRequest.name();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceConflictException("Category already exists");
        }

        Category category = CategoryMapper.toEntity(categoryCreateRequest);
        try {
            Category saved = categoryRepository.saveAndFlush(category);
            log.info("Added category with categoryId = {} and categoryName = {}", saved.getId(), saved.getName());
            return CategoryMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException(
                    "Category already exists"
            );
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        if(categoryUpdateRequest == null){
            throw new BadRequestException("Category request is required");
        }

        String name = categoryUpdateRequest.name();
        Category existing = findCategory(categoryId);

        String oldName =  existing.getName();
        if (oldName.equals(name)) {
            return CategoryMapper.toResponse(existing);
        }

        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(name, categoryId)) {
            throw new ResourceConflictException("Category already exists");
        }
        try {
            existing.rename(name);
            categoryRepository.flush();
        }
        catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException("Category already exists");
        }
        log.info(
                "Category updated: id={}, oldName={}, newName={}",
                categoryId,
                oldName,
                existing.getName()
        );
        return CategoryMapper.toResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = findCategory(categoryId);

        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryMapper::toResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);

        try{
            categoryRepository.delete(category);
            categoryRepository.flush();
        }
        catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException("Cannot delete category with associated products");
        }
        log.info("Category deleted: id={}", categoryId);

    }

    private Category findCategory(Long categoryId){
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return categoryRepository.existsByNameIgnoreCase(TextNormalize.normalize(name));
    }
}
