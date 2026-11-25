package com.webshop.category;

import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.persistence.ConstraintViolations;
import com.webshop.common.util.SafePageables;
import com.webshop.common.util.TextNormalize;
import com.webshop.product.ProductRepository;
import com.webshop.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name");
    private static final Set<String> ALLOWED_ADMIN_SORT_FIELDS =
            Set.of("id", "name", "status", "createdAt", "updatedAt");
    private static final String NAME_CONSTRAINT = "uk_categories_name";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static RuntimeException translateNameConflict(DataIntegrityViolationException e) {
        return ConstraintViolations.isViolationOf(e, NAME_CONSTRAINT)
                ? new ResourceConflictException("Category already exists")
                : e;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse addCategory(CategoryCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Category request is required");
        }

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResourceConflictException("Category already exists");
        }

        try {
            Category saved = categoryRepository.saveAndFlush(CategoryMapper.toEntity(request));
            log.info("Category created: id={}, name={}", saved.getId(), saved.getName());
            return CategoryMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw translateNameConflict(e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Category request is required");
        }

        String name = request.name();
        Category existing = findActiveCategory(categoryId);

        String oldName = existing.getName();
        if (oldName.equals(name)) {
            return CategoryMapper.toResponse(existing);
        }

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, categoryId)) {
            throw new ResourceConflictException("Category already exists");
        }

        try {
            existing.rename(name);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw translateNameConflict(e);
        }

        log.info("Category updated: id={}, oldName={}, newName={}", categoryId, oldName, existing.getName());
        return CategoryMapper.toResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        return CategoryMapper.toResponse(findActiveCategory(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_SORT_FIELDS, Sort.by("name"));
        return categoryRepository.findAllByStatus(CategoryStatus.ACTIVE, safe).map(CategoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CategoryAdminResponse> adminSearch(CategoryStatus status, Pageable pageable) {
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_ADMIN_SORT_FIELDS, Sort.by("name"));
        Page<Category> page = (status == null)
                ? categoryRepository.findAll(safe)
                : categoryRepository.findAllByStatus(status, safe);

        return page.map(CategoryMapper::toAdminResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        Category category = findActiveCategory(categoryId);

        if (productRepository.existsByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE)) {
            throw new ResourceConflictException(
                    "Category " + categoryId + " still has active products and cannot be archived");
        }

        category.archive();
        log.info("Category archived: id={}", categoryId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse activateCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        category.activate();
        log.info("Category activated: id={}", categoryId);
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return categoryRepository.existsByNameIgnoreCase(TextNormalize.normalize(name));
    }

    private Category findActiveCategory(Long categoryId) {
        return categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }
}