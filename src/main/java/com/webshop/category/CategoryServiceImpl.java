package com.webshop.category;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        if(categoryDTO == null)throw new IllegalArgumentException("categoryDTO cannot be null");

        Category category = CategoryMapper.toEntity(categoryDTO);
        Category saved =  categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        if(categoryId == null)throw  new IllegalArgumentException("categoryId cannot be null");
        if(categoryDTO == null)throw new IllegalArgumentException("categoryDTO cannot be null");

        Category existing = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));

        existing.setName(categoryDTO.getName());
        existing.setId(categoryId);

        Category updated = categoryRepository.save(existing);
        return  CategoryMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        if(categoryId == null)throw new IllegalArgumentException("categoryId cannot be null");

        Category category = categoryRepository
                            .findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));

        return CategoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        if(categoryId == null)throw new IllegalArgumentException("categoryId cannot be null");

        if(!categoryRepository.existsById(categoryId))return false;

        categoryRepository.deleteById(categoryId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        if(name == null || name.isEmpty())throw new  IllegalArgumentException("name cannot be null or empty");
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}
