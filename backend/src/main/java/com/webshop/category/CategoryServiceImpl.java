package com.webshop.category;

import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        if(categoryRepository.existsByNameIgnoreCase(categoryDTO.getName()))throw new ResourceConflictException("Category already exists");

        Category category = CategoryMapper.toEntity(categoryDTO);
        Category saved =  categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existing = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        existing.setName(categoryDTO.getName());

        Category updated = categoryRepository.save(existing);
        return  CategoryMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository
                            .findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

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
    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId))throw new ResourceNotFoundException("Category not found");

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}
